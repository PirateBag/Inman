package com.inman.business;

import com.inman.controller.OrderLineItemController;
import com.inman.controller.Utility;
import com.inman.entity.ActivityState;
import com.inman.entity.Item;
import com.inman.entity.OrderLineItem;
import com.inman.entity.Text;
import com.inman.model.request.CrudBatch;
import com.inman.model.request.ItemCrudBatch;
import com.inman.model.request.OrderLineItemRequest;
import com.inman.model.response.*;
import com.inman.model.rest.ErrorLine;
import com.inman.model.rest.ItemUpdateRequest;
import com.inman.repository.ItemRepository;
import com.inman.repository.OrderLineItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.inman.controller.OrderLineItemController.OrderLineItem_AllOrders;

@Service
public class OrderLineItemService {
    private static final Logger logger = LoggerFactory.getLogger(OrderLineItemService.class);
    private final ItemRepository itemRepository;

    OrderLineItemRepository orderLineItemRepository;

    private void outputInfo(String message, ResponsePackage<OrderLineItem> responsePackage ) {
        logger.info(message);
        responsePackage.getErrors().add( new ErrorLine(1, message) );
    }
    private void outputError(String message, ResponsePackage<OrderLineItem> responsePackage ) {
        logger.info(message);
        responsePackage.getErrors().add( new ErrorLine(1, message) );
    }

    public OrderLineItemService(OrderLineItemRepository orderLineItemRepository, ItemRepository itemRepository) {
        this.orderLineItemRepository = orderLineItemRepository;
        this.itemRepository = itemRepository;
    }

//    private String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
//        var detailedMessage = dataIntegrityViolationException.getMessage();
//        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
//            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
//        }
//        return detailedMessage;
//    }
//
//    private void updateMaxDepthOf(BomPresent updatedBom ) {
//        Item component = itemRepository.findById(updatedBom.getChildId());
//        Item parent = itemRepository.findById(updatedBom.getParentId());
//
//        if (component.getMaxDepth() <= parent.getMaxDepth()) {
//            int newMaxDepth = parent.getMaxDepth() + 1;
//            logger.info("{} depth changing from {} to {}" + 1, component.getId(), component.getMaxDepth(), parent.getMaxDepth());
//            component.setMaxDepth(newMaxDepth);
//            itemRepository.save(component);
//            return;
//        }
//        logger.info("{} depth not changing {} to {}" + 1, component.getId(), component.getMaxDepth(), parent.getMaxDepth());
//    }
//
//
////
//    private void change(BomPresent updatedBom, BomResponse bomResponse, int lineNumber) {
//        var oldBom = bomRepository.findById(updatedBom.getId());
//        String message = "";
//        if (oldBom.isEmpty()) {
//            message = "Unable to retrieve the original Bom instance for id " + updatedBom.getId();
//            bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
//            logger.error(message);
//            throw new RuntimeException(message);
//        }
//
//        if (updatedBom.getQuantityPer() == oldBom.get().getQuantityPer()) {
//            message = "Bom " + updatedBom.getId() + " quantityPer field did not change.";
//            logger.warn(message);
//            bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
//        } else {
//            logger.info("Bom " + updatedBom.getId() + " quantityPer was updated from " + oldBom.get().getQuantityPer() + " to " + updatedBom.getQuantityPer());
//            oldBom.get().setQuantityPer(updatedBom.getQuantityPer());
//
//            bomRepository.save(oldBom.get());
//            var refreshedBom = bomPresentRepository.findById(updatedBom.getId());
//            refreshedBom.setActivityState(ActivityState.CHANGE);
//            bomResponse.getData().add(refreshedBom);
//        }
//
//        updateMaxDepthOf(updatedBom );
//    }
//
    private void insert( OrderLineItem orderLineItem,  ResponsePackage<OrderLineItem> oliResponse, int lineNumber) {
        String message;
        OrderLineItem updatedOrderLineItem;
        try {
            Item item = itemRepository.findById( orderLineItem.getItemId() );
            if ( validateOrderLineItemForMOInsertion(orderLineItem, oliResponse, item) > 0 ) {
                outputError( "Validation on " + orderLineItem + " failed", oliResponse );
                throw new RuntimeException( "OrderLineItem validation failed with check logs" );
            };

            updatedOrderLineItem = orderLineItemRepository.save(orderLineItem);
            logger.info( "inserted " + updatedOrderLineItem );
            oliResponse.getData().add( updatedOrderLineItem );
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to insert " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputError( message, oliResponse );
        }
    }

    private void delete( OrderLineItem orderLineItem,  ResponsePackage<OrderLineItem> oliResponse ) {
        String message;
        try {
            Optional<OrderLineItem> orderLineItemFromRepository = orderLineItemRepository.findById( orderLineItem.getId() );

            if ( orderLineItemFromRepository.isPresent() ) {
                orderLineItemRepository.delete( orderLineItemFromRepository.get() );
            } else {
                outputError( "Validation on " + orderLineItem + " failed", oliResponse );
                throw new RuntimeException( "OrderLineItem validation failed with check logs" );
            }

            logger.info(  orderLineItem.getActivityState() + " " + orderLineItemFromRepository );
            orderLineItemFromRepository.get().setActivityState( orderLineItem.getActivityState() );
            oliResponse.getData().add( orderLineItemFromRepository.get() );
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to " + orderLineItem.getActivityState() + " " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputError( message, oliResponse );
        }
    }


    private int validateOrderLineItemForMOInsertion(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse, Item item) {
        int numberOfMessages = 0;
        if ( item == null ) {
            outputInfo( "Item " + orderLineItem.getItemId() + " cannot be found", oliResponse);
            numberOfMessages++;
        }

        if ( orderLineItem.getParentOliId() != 0 ) {
            outputInfo( "Order has a parent item:  " + orderLineItem, oliResponse);
            numberOfMessages++;
        }

        if ( item.getSourcing().compareTo( Item.SOURCE_MAN ) != 0  ) {
            outputInfo("Item is is not MANufactured: " + item, oliResponse);
            numberOfMessages++;
        }

        if ( orderLineItem.getQuantityOrdered() < 0.0 ) {
            outputInfo("Order Quantity " + orderLineItem + " must be greater than 0.  ", oliResponse);
            numberOfMessages++;
        }

        if ( orderLineItem.getQuantityAssigned() != 0.0 ) {
            outputInfo("Quantity Assigned must be zero only at creation. ", oliResponse);
            numberOfMessages++;
        }
        return numberOfMessages;
    }

    @Transactional
    public ResponsePackage<OrderLineItem> applyCrud( OrderLineItemRequest crudBatch ) {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();

        for ( OrderLineItem orderLineItem : crudBatch.rows() ) {

            logger.info("{} on {}", orderLineItem.getActivityState(), orderLineItem);

            if (orderLineItem.getActivityState() == ActivityState.INSERT) {
                insert (orderLineItem, responsePackage, 1 );
            } else if ( orderLineItem.getActivityState() == ActivityState.DELETE ||
                    orderLineItem.getActivityState() == ActivityState.DELETE_SILENT) {
                delete( orderLineItem, responsePackage);
            } /*  else if ( orderLineItem.getActivityState() == ActivityState.CHANGE ) {
                changeItem(orderLineItem, responsePackage);
            }  */  else {
                logger.info("{} was ignored because of unknown ActivityState", orderLineItem );
            }
        }
        return responsePackage;
    }

    public TextResponse orderReport( int orderId ) {
        if (orderId != OrderLineItem_AllOrders) {
            throw new RuntimeException("Illegal order id of " + orderId);
        }
        TextResponse textResponse = new TextResponse();
        List<com.inman.entity.OrderLineItem> reportList = orderLineItemRepository.findAll();

        logger.info(OrderLineItem.header );
        for (com.inman.entity.OrderLineItem orderLineItem : reportList) {
            logger.info( orderLineItem.toString() );
            textResponse.getData().add( new Text(orderLineItem.toString() ) );
        }
        return textResponse;
    }
}
