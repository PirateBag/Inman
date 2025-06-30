package com.inman.business;

import com.inman.controller.Utility;
import com.inman.entity.*;
import com.inman.model.request.OrderLineItemRequest;
import com.inman.model.response.*;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.DdlRepository;
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
    private final DdlRepository ddlRepository;
    private final BomPresentRepository bomPresentRepository;


    @Autowired
    public OrderLineItemService(ItemRepository itemRepository, OrderLineItemRepository orderLineItemRepository,
                                DdlRepository ddlRepository, BomPresentRepository bomPresentRepository ) {
        this.itemRepository = itemRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.ddlRepository = ddlRepository;
        this.bomPresentRepository = bomPresentRepository;
    }

    private void outputInfo(String message, ResponsePackage<?> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
    }

    private void outputError(String message, ResponsePackage<?> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
        throw new RuntimeException(message);
    }

    private void insert(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse, int lineNumber) {
        String message;
        OrderLineItem updatedOrderLineItem;
        try {
            Item item = itemRepository.findById(orderLineItem.getItemId());
            LocalDate completedDate = LocalDate.parse(orderLineItem.getStartDate(), DATE_FORMATTER).plusDays(item.getLeadTime());
            orderLineItem.setCompleteDate(completedDate.format(DATE_FORMATTER));
            if (validateOrderLineItemForMOInsertion(orderLineItem, oliResponse, item) > 0) {
                outputInfo("Validation on " + orderLineItem + " failed", oliResponse);
            }

            updatedOrderLineItem = orderLineItemRepository.save(orderLineItem);
            logger.info("inserted adjusted: {}", updatedOrderLineItem);
            oliResponse.getData().add(updatedOrderLineItem);

            if ( orderLineItem.getOrderState() == OrderState.OPEN ) {
                addLineItemsToOrder( updatedOrderLineItem, oliResponse );
            }
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to insert " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputError(message, oliResponse);
        } catch (RuntimeException runtimeException) {
            outputError( "Unable to insert " + orderLineItem + ":" + runtimeException.getMessage(), oliResponse);
        }

    }

    /**
     * Add orderLineItems to the parent owner based on the BOM of the item associated with the parent.
     *
     * @param id of the newly inserted order.k
     * @param oliResponse final response message, modified by side effect.
     *
     */
    private void addLineItemsToOrder( OrderLineItem parentOli , ResponsePackage<OrderLineItem> oliResponse) {
        BomPresent[] childrenOfItem = bomPresentRepository.findByParentId( parentOli.getItemId() );

        for ( BomPresent bomPresent : childrenOfItem ) {
            OrderLineItem oli = new OrderLineItem();
            oli.setItemId( bomPresent.getChildId() );
            oli.setQuantityOrdered( parentOli.getQuantityOrdered() * bomPresent.getQuantityPer() );
            oli.setStartDate(  parentOli.getStartDate() );
            oli.setCompleteDate( parentOli.getCompleteDate() );
            oli.setParentOliId( parentOli.getId() );
            oli.setQuantityAssigned(  0.0 );
            oli.setActivityState( ActivityState.INSERT );

            var updatedOli = orderLineItemRepository.save(oli);
            logger.info( updatedOli.toString()  );
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

            if ( orderLineItem.getOrderState() == OrderState.OPEN ) {
                outputError( "Delete on open order is prohibited.", oliResponse );
            }

            List<OrderLineItem> childrenOfOrder = orderLineItemRepository.findByParentOliId( orderLineItem.getId() );
            if ( childrenOfOrder.size() > 0 ) {
                outputError( "Deletion on " + orderLineItem + " failed because it has children.", oliResponse);
            }

            orderLineItemFromRepository.get().setActivityState(orderLineItem.getActivityState());
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

            var sqlCommand = createUpdateByRowIdStatement( "order_line_item", 1, fieldsToUpdate );
            logger.info( "Update Statement: " + sqlCommand );
            ddlRepository.executeDynamicDML( sqlCommand );
            logger.info( "Update completed. " + sqlCommand );
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

    private void changeViaJpa(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse) {
        String message;

        try {
            Optional<OrderLineItem> orderLineItemFromRepository = orderLineItemRepository.findById(orderLineItem.getId());

            if (orderLineItemFromRepository.isPresent()) {
                if (orderLineItem.getItemId() != orderLineItemFromRepository.get().getItemId()) {
                    outputError("Item Changed in order.  Try delete/insert instead", oliResponse);
                }
                var countOfChanges = updateOldFromNew( orderLineItemFromRepository.get(), orderLineItem, oliResponse );
                logger.info("Number Of Changes: {}", countOfChanges);
                orderLineItemRepository.save(orderLineItemFromRepository.get());
            } else {
                outputError( "Unable to find " + orderLineItem, oliResponse);
            }

            orderLineItemFromRepository.get().setActivityState(orderLineItem.getActivityState());
            oliResponse.getData().add(orderLineItemFromRepository.get() );
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
            rValue.put( "quantity_ordered", String.valueOf( newOli.getQuantityOrdered() ) );
        }
        if ( oldOli.getQuantityAssigned() != newOli.getQuantityAssigned() ) {
            rValue.put( "quantity_assigned", String.valueOf( newOli.getQuantityAssigned() ) );
        }
        if ( normalize( oldOli.getStartDate()).compareTo( normalize( newOli.getStartDate() ) ) != 0 ) {
            rValue.put( "start_date", "'" + newOli.getStartDate() + "'");
        }
        if ( normalize( oldOli.getCompleteDate()).compareTo( normalize(newOli.getCompleteDate()) ) != 0 ) {
            rValue.put( "Complete_date", "'" + newOli.getCompleteDate() + "'" );
        }
        return rValue;
    }

    /**
     * @param oldOli old order line item from database.
     * @param newOli new orderLineItem from request.
     * @param oliResponse build up our response message
     * @return count of number of changes.
     */
    public  int updateOldFromNew( OrderLineItem oldOli,
                                                       OrderLineItem newOli, ResponsePackage<OrderLineItem> oliResponse  ) {

        int countOfChanges = 0;
        if ( oldOli.getId() != newOli.getId() ) {
            outputInfo( "Order Id is different",  oliResponse );
        }
        if ( oldOli.getItemId() != newOli.getItemId() ) {
            outputInfo( "Item Ids are not the same.", oliResponse );
        }
        if ( oldOli.getQuantityOrdered() != newOli.getQuantityOrdered() ) {
            countOfChanges++;
            oldOli.setQuantityOrdered( newOli.getQuantityOrdered() );
        }
        if ( oldOli.getQuantityAssigned() != newOli.getQuantityAssigned() ) {
            countOfChanges++;
            oldOli.setQuantityAssigned( newOli.getQuantityAssigned() );
        }
        if ( normalize( oldOli.getStartDate()).compareTo( normalize( newOli.getStartDate() ) ) != 0 ) {
            countOfChanges++;
            oldOli.setStartDate( newOli.getStartDate() );
        }
        if ( normalize( oldOli.getCompleteDate()).compareTo( normalize(newOli.getCompleteDate()) ) != 0 ) {
            countOfChanges++;
            oldOli.setCompleteDate( newOli.getCompleteDate() );
        }
        return countOfChanges;
    }

    @Transactional
    public ResponsePackage<OrderLineItem> applyCrud(OrderLineItemRequest crudBatch,
                                                    ResponsePackage<OrderLineItem> responsePackage ) {

        for (OrderLineItem orderLineItem : crudBatch.rows()) {
            logger.info("{} on {}", orderLineItem.getActivityState(), orderLineItem);

            if (orderLineItem.getActivityState() == ActivityState.INSERT) {
                insert(orderLineItem, responsePackage, 1);
            } else if (orderLineItem.getActivityState() == ActivityState.DELETE ) {
                delete(orderLineItem, responsePackage);
            } else if ( orderLineItem.getActivityState() == ActivityState.CHANGE ) {
                //  change(orderLineItem, responsePackage);
                changeViaJpa( orderLineItem, responsePackage);
            }  else {
                logger.info("{} was ignored because of unknown ActivityState", orderLineItem);
            }
        }
        return responsePackage;
    }

    public void generateRecursiveOrderReport( long orderId, TextResponse textResponse, int level  ) {
        String report;
        Optional<OrderLineItem> oli = orderLineItemRepository.findById( orderId );

        if (oli.isEmpty() ) {
            outputInfo( "Can't find order " + orderId, textResponse  );
            return;
        }
        Item item = itemRepository.findById( oli.get().getItemId());
        if ( item == null ) {
            outputInfo( "Can't find item " + oli.get().getItemId() + " referenced by oli " + oli.get().getId(), textResponse  );
            return;
        }

        String headerFormat = "%-26s  %8s %8s %9s %9s %7s";
        String lineFormat = "%-26s  %8.2f %8.2f %9s %9s %7s";

        if ( level == 0 ) {
            report = String.format( headerFormat, "Item", "Ordered", "Assigned", "Start", "Complete", "State" );
            logger.info( report );
            textResponse.getData().add( new Text( report ) );
        }


        report = String.format( lineFormat, Common.spacesForLevel( level) + item.getSummaryId(), oli.get().getQuantityOrdered(), oli.get().getQuantityAssigned(),
                oli.get().getStartDate(), oli.get().getCompleteDate(), oli.get().getOrderState()  );
        textResponse.getData().add( new Text( report ) );

        List<OrderLineItem> childOrderLineItems = orderLineItemRepository.findByParentOliId( oli.get().getId());
        for ( OrderLineItem childOrderLineItem : childOrderLineItems ) {
            generateRecursiveOrderReport( childOrderLineItem.getId(), textResponse, level+1 );
        }
    }


    public TextResponse orderReport(long orderId) {
        TextResponse textResponse = new TextResponse();
        if (orderId == OrderLineItem_AllOrders ) {
            List<com.inman.entity.OrderLineItem> reportList = orderLineItemRepository.findAll();

            logger.info(OrderLineItem.header);
            for (com.inman.entity.OrderLineItem orderLineItem : reportList) {
                logger.info(orderLineItem.toString());
                textResponse.getData().add(new Text(orderLineItem.toString()));
            }
        } else {
            generateRecursiveOrderReport( orderId, textResponse, 0  );
        }
        return textResponse;
    }
}
