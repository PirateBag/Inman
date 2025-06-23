package com.inman.business;

import com.inman.controller.Utility;
import com.inman.entity.*;
import com.inman.model.request.OrderLineItemRequest;
import com.inman.model.response.*;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.ItemRepository;
import com.inman.repository.OrderLineItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static com.inman.controller.OrderLineItemController.OrderLineItem_AllOrders;
import static com.inman.controller.Utility.DATE_FORMATTER;
import static com.inman.controller.Utility.normalize;
import static com.inman.repository.DdlRepository.createUpdateByRowIdStatement;

@Service
public class OrderLineItemService {
    private static final Logger logger = LoggerFactory.getLogger(OrderLineItemService.class);
    private final ItemRepository itemRepository;
    private final OrderLineItemRepository orderLineItemRepository;


    @Autowired
    public OrderLineItemService( ItemRepository itemRepository, OrderLineItemRepository orderLineItemRepository ) {
        this.itemRepository = itemRepository;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    private void outputInfo(String message, ResponsePackage<OrderLineItem> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
    }

    private void outputError(String message, ResponsePackage<OrderLineItem> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
        throw new RuntimeException(message);
    }

    public OrderLineItemService(OrderLineItemRepository orderLineItemRepository, ItemRepository itemRepository) {
        this.orderLineItemRepository = orderLineItemRepository;
        this.itemRepository = itemRepository;
    }

    private void insert(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse, int lineNumber) {
        String message;
        OrderLineItem updatedOrderLineItem;
        try {

            Item item = itemRepository.findById(orderLineItem.getItemId());
            LocalDate completedDate = LocalDate.parse(orderLineItem.getStartDate(), DATE_FORMATTER).plusDays(item.getLeadTime());
            orderLineItem.setCompleteDate(completedDate.format(DATE_FORMATTER));
            if (validateOrderLineItemForMOInsertion(orderLineItem, oliResponse, item) > 0) {
                outputError("Validation on " + orderLineItem + " failed", oliResponse);
            }

            updatedOrderLineItem = orderLineItemRepository.save(orderLineItem);
            logger.info("inserted adjusted: {}", updatedOrderLineItem);
            oliResponse.getData().add(updatedOrderLineItem);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to insert " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputError(message, oliResponse);
        }
    }

    private void delete(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse) {
        String message;
        try {
            Optional<OrderLineItem> orderLineItemFromRepository = orderLineItemRepository.findById( orderLineItem.getId());

            if (orderLineItemFromRepository.isPresent()) {
                orderLineItemRepository.delete(orderLineItemFromRepository.get());
            } else {
                outputError("Unable to find " + orderLineItem, oliResponse);
            }
            logger.info(orderLineItem.getActivityState() + " " + orderLineItemFromRepository);
            orderLineItem.setActivityState(orderLineItemFromRepository.get().getActivityState());
            oliResponse.getData().add(orderLineItemFromRepository.get());
            orderLineItemRepository.deleteById( orderLineItem.getId());
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to " + orderLineItem.getActivityState() + " " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputError(message, oliResponse);
        }
    }

    private void change(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse) {
        String message;
        try {
            Optional<OrderLineItem> orderLineItemFromRepository = orderLineItemRepository.findById(orderLineItem.getId());
            if (orderLineItemFromRepository.isPresent()) {
                orderLineItemRepository.delete(orderLineItemFromRepository.get());
            } else {
                outputError("Unable to find " + orderLineItem, oliResponse);
            }

            if (orderLineItem.getItemId() != orderLineItemFromRepository.get().getItemId()) {
                outputError("Item Changed in order.  Try delete/insert instead", oliResponse);
            }

            Map<String,String> fieldsToUpdate =
                    createMapFromOldAndNew( orderLineItemFromRepository.get(), orderLineItem, oliResponse );
            logger.info( "Map is: " + fieldsToUpdate );

            var SqlString = createUpdateByRowIdStatement( "OrderLineItem", 1, fieldsToUpdate );
            logger.info( "Update Statement: " + SqlString );

            logger.info( "Just Before:  " + orderLineItem.getActivityState() + " " + orderLineItemFromRepository.get());
            orderLineItemRepository.save(orderLineItemFromRepository.get());
            oliResponse.getData().add(orderLineItem);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to " + orderLineItem.getActivityState() + " " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputError(message, oliResponse);
        } catch (Exception e) {
            logger.error( "Unexpected exception " + e.getMessage() );
            throw new RuntimeException(e);
        }
    }


    public int validateOrderLineItemForMOInsertion(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse, Item item) {
        int numberOfMessages = 0;
        if (item == null) {
            outputInfo("Order references Item " + orderLineItem.getItemId() + " that cannot be found", oliResponse);
            numberOfMessages++;

            //  Empty Item will break other tests.  Proceed no further.
            return numberOfMessages;
        }

        if (orderLineItem.getParentOliId() != 0) {
            outputInfo("Order has a parent item:  " + orderLineItem, oliResponse);
            numberOfMessages++;
        }

        if (item.getSourcing( ).compareTo(Item.SOURCE_MAN) != 0) {
            outputInfo("Item is is not MANufactured: " + item, oliResponse);
            numberOfMessages++;
        }

        if (orderLineItem.getQuantityOrdered() <= 0.0) {
            outputInfo("Order Quantity " + orderLineItem + " must be greater than 0.", oliResponse);
            numberOfMessages++;
        }

        if (orderLineItem.getQuantityAssigned() != 0.0) {
            outputInfo("Quantity Assigned must be zero.", oliResponse);
            numberOfMessages++;
        }

        return numberOfMessages;
    }

    public  Map<String,String> createMapFromOldAndNew( OrderLineItem oldOli,
            OrderLineItem newOli, ResponsePackage<OrderLineItem> oliResponse  ) {
//        //	0 when an MO Order header.  Non 0 when a detail of either MO or PO.
//        int parentOliId;
//        OrderState orderState = OrderState.PLANNED;
//        DebitCreditIndicator debitCreditIndicator = com.inman.entity.DebitCreditIndicator.ADDS_TO_BALANCE;
        SortedMap<String,String> rValue = new TreeMap<String,String>();

        if ( oldOli.getId() != newOli.getId() ) {
            outputInfo( "Order Id is different",  oliResponse );
        }
        if ( oldOli.getItemId() != newOli.getItemId() ) {
            outputInfo( "Item Ids are not the same.", oliResponse );
        }
        if ( oldOli.getQuantityOrdered() != newOli.getQuantityOrdered() ) {
            rValue.put( "QuantityOrders", String.valueOf( newOli.getQuantityOrdered() ) );
        }
        if ( oldOli.getQuantityAssigned() != newOli.getQuantityAssigned() ) {
            rValue.put( "QuantityAssigned", String.valueOf( newOli.getQuantityAssigned() ) );
        }
        if ( normalize( oldOli.getStartDate()).compareTo( normalize( newOli.getStartDate() ) ) != 0 ) {
            rValue.put( "StartDate", "'" + newOli.getStartDate() + "'");
        }
        if ( normalize( oldOli.getCompleteDate()).compareTo( normalize(newOli.getCompleteDate()) ) != 0 ) {
            rValue.put( "CompletedDate", "'" + newOli.getCompleteDate() + "'" );
        }
        return rValue;
    }

    @Transactional
    public ResponsePackage<OrderLineItem> applyCrud(OrderLineItemRequest crudBatch) {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();

        for (OrderLineItem orderLineItem : crudBatch.rows()) {

            logger.info("{} on {}", orderLineItem.getActivityState(), orderLineItem);


            if (orderLineItem.getActivityState() == ActivityState.INSERT) {
                insert(orderLineItem, responsePackage, 1);
            } else if (orderLineItem.getActivityState() == ActivityState.DELETE ) {
                delete(orderLineItem, responsePackage);
            } else if ( orderLineItem.getActivityState() == ActivityState.CHANGE ) {
                change(orderLineItem, responsePackage);
            }  else {
                logger.info("{} was ignored because of unknown ActivityState", orderLineItem);
            }
        }
        return responsePackage;
    }

    public TextResponse orderReport(int orderId) {
        if (orderId != OrderLineItem_AllOrders) {
            throw new RuntimeException("Illegal order id of " + orderId);
        }
        TextResponse textResponse = new TextResponse();
        List<com.inman.entity.OrderLineItem> reportList = orderLineItemRepository.findAll();

        logger.info(OrderLineItem.header);
        for (com.inman.entity.OrderLineItem orderLineItem : reportList) {
            logger.info(orderLineItem.toString());
            textResponse.getData().add(new Text(orderLineItem.toString()));
        }
        return textResponse;
    }
}
