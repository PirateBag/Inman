package com.inman.service;

import com.inman.controller.Messages;
import com.inman.controller.Utility;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.entity.OrderLineItem;
import com.inman.entity.Text;
import com.inman.model.request.OrderLineItemRequest;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.TextResponse;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import com.inman.repository.OrderLineItemRepository;
import enums.CrudAction;
import enums.OrderState;
import enums.OrderType;
import enums.SourcingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static com.inman.controller.Messages.*;
import static com.inman.controller.OrderLineItemController.OrderLineItem_AllOrders;
import static com.inman.controller.Utility.*;
import static com.inman.service.ReflectionHelpers.compareObjects;

@Service
public class OrderLineItemService {

    private static final int MAX_REPORT_LINES = 5;

    private static final Logger logger = LoggerFactory.getLogger(OrderLineItemService.class);
    private final ItemRepository itemRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final BomPresentRepository bomPresentRepository;


    @Autowired
    public OrderLineItemService(ItemRepository itemRepository, OrderLineItemRepository orderLineItemRepository,
                                BomPresentRepository bomPresentRepository ) {
        this.itemRepository = itemRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.bomPresentRepository = bomPresentRepository;
    }

    private void outputErrorAndThrow(String message, ResponsePackage<?> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
        throw new RuntimeException(message);
    }

    private void insert(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse ) {
        String message;
        OrderLineItem updatedOrderLineItem;

        try {
            Item item = itemRepository.findById(orderLineItem.getItemId());

            LocalDate derviedStartOrCompleted ;

            if ( isNullOrEmpty( orderLineItem.getStartDate() ) && isNullOrEmpty( orderLineItem.getCompleteDate() ) ) {
                outputErrorAndThrow( "A line item must have either startDate or completedDate", oliResponse );
            } else if ( isNullOrEmpty( orderLineItem.getStartDate()  ) ) {
                derviedStartOrCompleted = LocalDate.parse(orderLineItem.getCompleteDate(), DATE_FORMATTER).minusDays(item.getLeadTime());
               orderLineItem.setStartDate( derviedStartOrCompleted.format(DATE_FORMATTER));
                logger.info("Derived start from completed less lead time." );
            } if ( isNullOrEmpty( orderLineItem.getCompleteDate() ) ) {
                derviedStartOrCompleted = LocalDate.parse(orderLineItem.getStartDate(), DATE_FORMATTER).plusDays(item.getLeadTime());
                orderLineItem.setCompleteDate( derviedStartOrCompleted.format(DATE_FORMATTER));
                logger.info("Derived completeDate from Start plus lead time." );
            }

            validateOrderLineItemForMOInsertion(orderLineItem, oliResponse, item);
            updatedOrderLineItem = orderLineItemRepository.save(orderLineItem);
            logger.info("inserted adjusted: {}", updatedOrderLineItem);
            oliResponse.getData().add(updatedOrderLineItem);

            if ( orderLineItem.getOrderState() == OrderState.OPEN ) {
                addLineItemsToOrder( updatedOrderLineItem, oliResponse );
            }
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to insert " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputErrorAndThrow(message, oliResponse);
        } catch (RuntimeException runtimeException) {
            outputErrorAndThrow( "Unable to insert " + orderLineItem + ":" + runtimeException.getMessage(), oliResponse);
        }

    }

    /**
     * Add orderLineItems to the order based on the BOM of the item associated with the order.
     *
     * @param parentOli   parent of the newly inserted order.
     * @param oliResponse final response message, modified by side effect.
     */
    private void addLineItemsToOrder(OrderLineItem parentOli, ResponsePackage<OrderLineItem> oliResponse) {
        Item item = itemRepository.findById(parentOli.getItemId());
        if (item == null ) {
            outputErrorAndThrow("Unable to find item with ID " + parentOli.getItemId(), oliResponse);
        }
        BomPresent[] childrenOfItem = bomPresentRepository.findByParentId(parentOli.getItemId());
        int numberOfAddedItems = 1;
        for (BomPresent bomPresent : childrenOfItem) {
            assert item != null;

            OrderLineItem oli = createOrderLineItem(parentOli, bomPresent, item );
            OrderLineItem updatedOli = orderLineItemRepository.save(oli);
            logger.info(updatedOli.toString());
            numberOfAddedItems++;
        }
        outputInfo("Added " + numberOfAddedItems + " line items to order " + parentOli.getId(), oliResponse, logger);
    }

    /**
     * Create a new OrderLineItem based on the parent OrderLineItem, BomPresent, and Item.
     *
     * @param parentOli  parent of the newly inserted order.
     * @param bomPresent the bomPresent of the current item.
     * @param item       the item corresponding to the parentOli itemId.
     * @return a new OrderLineItem created from the given parameters.
     */
    private OrderLineItem createOrderLineItem(OrderLineItem parentOli, BomPresent bomPresent, Item item) {
        OrderLineItem oli = new OrderLineItem();
        oli.setItemId(bomPresent.getChildId());
        oli.setQuantityOrdered(parentOli.getQuantityOrdered() * bomPresent.getQuantityPer());
        oli.setCompleteDate(parentOli.getStartDate());
        LocalDate derivedStart = LocalDate.parse(parentOli.getStartDate(), DATE_FORMATTER).minusDays(item.getLeadTime());
        oli.setStartDate(derivedStart.format(DATE_FORMATTER));
        oli.setParentOliId(parentOli.getId());
        oli.setQuantityAssigned(0.0);
        oli.setCrudAction(parentOli.getCrudAction());
        oli.setOrderState(parentOli.getOrderState());
        oli.setOrderType(item.getSourcing() == SourcingType.MAN ? OrderType.MODET : OrderType.PO);
        return oli;
    }

    private void delete(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse) {
        String message;
        try {
            Optional<OrderLineItem> orderLineItemFromRepository = orderLineItemRepository.findById( orderLineItem.getId());
            if (orderLineItemFromRepository.isPresent()) {
                orderLineItemRepository.delete(orderLineItemFromRepository.get());
            } else {

                outputErrorAndThrow(Messages.ITEM_REF_NOT_FOUND.formatted( "OrderLineItem:Delete",orderLineItem.getItemId()), oliResponse);
            }

            if ( orderLineItem.getOrderState() == OrderState.OPEN ) {
                outputErrorAndThrow( "Delete on open order is prohibited.", oliResponse );
            }

            List<OrderLineItem> childrenOfOrder = orderLineItemRepository.findByParentOliId( orderLineItem.getId() );
            if (!childrenOfOrder.isEmpty()) {
                outputErrorAndThrow( "Deletion on " + orderLineItem + " failed because it has children.", oliResponse);
            }

            orderLineItemFromRepository.get().setCrudAction(orderLineItem.getCrudAction());
            oliResponse.getData().add(orderLineItemFromRepository.get());
            orderLineItemRepository.deleteById( orderLineItem.getId());

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to " + orderLineItem.getCrudAction() + " " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputErrorAndThrow(message, oliResponse);
        }
    }


    private void changeViaJpa(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse) {
        String message;

        try {
            OrderLineItem oldOrderLineItem;
            {
                Optional<OrderLineItem> oldOrderLineFromRepository = orderLineItemRepository.findById(orderLineItem.getId());

                if (oldOrderLineFromRepository.isEmpty()) {
                    outputErrorAndThrow("Unable to find order " + orderLineItem, oliResponse);
                }

                // Make a copy of the old item...
                oldOrderLineItem = new OrderLineItem(oldOrderLineFromRepository.get());
            }
            var changeMap = ReflectionHelpers.compareObjects( oldOrderLineItem, orderLineItem );
            logger.info("Number Of Changes: {}", changeMap.size() );
            if ( changeMap.isEmpty() ) {
                outputErrorAndThrow("Order does not appear to be changed:  " + orderLineItem, oliResponse);
            }

            if ( oldOrderLineItem.getOrderState() == OrderState.OPEN
                && orderLineItem.getItemId() != oldOrderLineItem.getItemId()) {
                        outputErrorAndThrow("Item Changed in Open order.  Try delete/insert instead", oliResponse);
                }

            orderLineItemRepository.save(orderLineItem);
            if ( changeMap.get( "orderState") != null ) {
                updateChildrenOfOrder( oldOrderLineItem, orderLineItem, oliResponse ) ;
            }

            oliResponse.getData().add(orderLineItem  );
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to " + orderLineItem.getCrudAction() + " " + orderLineItem + ":" +
                    Utility.generateErrorMessageFrom(dataIntegrityViolationException);
            outputErrorAndThrow(message, oliResponse);
        } catch (Exception e) {
            logger.error("Unexpected exception {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Identify what type of orderStatus changes were made and
     * planned->open:  Create child items.
     * planned->closed:
     * open->planned:  delete child items.
     * open->close: update children with new order status.
     * close->plan:  delete child items.
     * close->open update children with new order state
     *
     * @param oldOli the OLI to be changed.
     * @param newOli new value of the oli.
     * @param oliResponse i/o parameter:  response message under contruction.
     */
    private void updateChildrenOfOrder(OrderLineItem oldOli, OrderLineItem newOli, ResponsePackage<OrderLineItem> oliResponse) {
        if ( oldOli.getOrderState() == newOli.getOrderState() ) {
            throw new RuntimeException( "No change in orderState " );
        }

        if ( oldOli.getOrderState() == OrderState.PLANNED ) {
            if ( newOli.getOrderState() == OrderState.OPEN ) {
                addLineItemsToOrder( newOli,  oliResponse);
            }
            if ( newOli.getOrderState() == OrderState.CLOSED ) {
                outputErrorAndThrow( "Cannot change state on order " + newOli.getId() + " from planned to close.  Try delete. " , oliResponse );
            }
        }

        if ( oldOli.getOrderState() == OrderState.OPEN ) {
            if ( newOli.getOrderState() == OrderState.CLOSED ) {
                updateLineItemsWithNewState( newOli, oliResponse );
            }
            if ( newOli.getOrderState() == OrderState.PLANNED ) {
                throwErrorIfChildrenArePresent( newOli, oliResponse );
            }
        }

        if ( oldOli.getOrderState() == OrderState.CLOSED ) {
            if ( newOli.getOrderState() == OrderState.PLANNED  ) {
                deleteChildLineItems( newOli, oliResponse );
            }
            if ( newOli.getOrderState() == OrderState.OPEN ) {
                updateLineItemsWithNewState( newOli, oliResponse );
            }
        }
    }

    private void deleteChildLineItems(OrderLineItem newOli, ResponsePackage<OrderLineItem> oliResponse) {
            List<OrderLineItem>  lineItems = orderLineItemRepository.findByParentOliId( newOli.getId() );
            int count = 1;
            for ( OrderLineItem oli : lineItems ) {
                oli.setOrderState( newOli.getOrderState() );
                orderLineItemRepository.delete(oli);
                count++;
            }
            outputInfo( "Removed " + count + " lines from order " + newOli, oliResponse, logger );
    }

    private void throwErrorIfChildrenArePresent( OrderLineItem newOli, ResponsePackage<OrderLineItem> oliResponse) {
        List<OrderLineItem>  lineItems = orderLineItemRepository.findByParentOliId( newOli.getId() );
        if ( lineItems.isEmpty() ) {
            logger.info( "Order " + newOli.getId() + " has no children to block this activity" );
        }
        outputErrorAndThrow( "Order " + newOli.getId() + " is being updated to planned, but has " + lineItems.size() + " children and the operation is cancelled", oliResponse);
    }


    /**
     * Visit each of the components of the newOli and change state to the same state as the parent.
     *
     * @param newOli
     * @param oliResponse
     */
    private void updateLineItemsWithNewState( OrderLineItem newOli, ResponsePackage<OrderLineItem> oliResponse ) {
        List<OrderLineItem>  lineItems = orderLineItemRepository.findByParentOliId( newOli.getId() );
        int count = 0;
       for ( OrderLineItem oli : lineItems ) {
           oli.setOrderState( newOli.getOrderState() );
           orderLineItemRepository.save(oli);
           count++;
       }
       outputInfo( "Updated " + count + " line items to state " + newOli.getOrderState(), oliResponse, logger );
    }


    /**
     * Validate a proposed MORDER for creation.
     *
     * @param orderLineItem
     * @param oliResponse
     * @param item
     *
     * Throws errors and logs as needed.
     */
    public void validateOrderLineItemForMOInsertion(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse, Item item ) {

        if (item == null) {
            Utility.outputErrorAndThrow( String.format( ITEM_REF_NOT_FOUND, "Order", orderLineItem.getItemId() ), oliResponse, logger );
        }

        //  Any type of item can appear as a MODET...
        if ( orderLineItem.getOrderType() != OrderType.MODET ) {

            //  But you can only put MAN items in a MOHEAD...
            if (item.getSourcing() == SourcingType.MAN && orderLineItem.getOrderType() != OrderType.MOHEAD) {
                Utility.outputErrorAndThrow(String.format(ITEM_MANUFACTURED, item.getSummaryId()), oliResponse, logger);
            }

            //  Or purchased items in a PO...
            if (item.getSourcing() == SourcingType.PUR && orderLineItem.getOrderType() != OrderType.PO) {
                Utility.outputErrorAndThrow(String.format(ITEM_PURCHASED, item.getSummaryId()), oliResponse, logger);
            }
        }

        if (orderLineItem.getQuantityOrdered() <= 0.0) {
           Utility.outputErrorAndThrow( String.format( QUANTITY_ORDERED_GT_0, orderLineItem.getQuantityOrdered(), item.getSummaryId() ), oliResponse, logger );
        }

        if (orderLineItem.getQuantityAssigned() != 0.0) {
            Utility.outputErrorAndThrow( String.format( QUANTITY_ASSIGNED_NON_0, orderLineItem.getQuantityOrdered(), item.getSummaryId() ), oliResponse, logger );
        }
    }

    /**
     * Compare two orderLineItem objects.  Compare each fields, and create a map of the name of any field that changed
     * and its new value.
     *
     * @param oldOli One of the two OLIs to compare.
     * @param newOli The other OLI to compare
     * @return of field names (key) and corresponding Object
     */
    public  Map<String,Object> createMapOfChangedValues(OrderLineItem oldOli,
                                                        OrderLineItem newOli ) {
        Map<String,Object[]> completeCompare = compareObjects( oldOli, newOli );
        Map<String,Object> rValue= new TreeMap<>();

        for ( String key : completeCompare.keySet() ) {
            rValue.put( key, completeCompare.get( key )[0] );
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
            outputInfo( "Order Id is different",  oliResponse, logger );
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
            logger.info("{} on {}", orderLineItem.getCrudAction(), orderLineItem);

            if (orderLineItem.getCrudAction() == CrudAction.INSERT) {
                insert(orderLineItem, responsePackage );
            } else if (orderLineItem.getCrudAction() == CrudAction.DELETE ) {
                delete(orderLineItem, responsePackage);
            } else if ( orderLineItem.getCrudAction() == CrudAction.CHANGE ) {
                //  change(orderLineItem, responsePackage);
                changeViaJpa( orderLineItem, responsePackage);
            }  else {
                logger.info("{} was ignored because of unknown CrudAction", orderLineItem);
            }
        }
        return responsePackage;
    }

    public void generateRecursiveOrderReport( long orderId, TextResponse textResponse, int level  ) {
        String report;
        Optional<OrderLineItem> oli = orderLineItemRepository.findById( orderId );

        if (oli.isEmpty() ) {
            outputInfo( "Can't find order " + orderId, textResponse, logger  );
            return;
        }
        Item item = itemRepository.findById( oli.get().getItemId());
        if ( item == null ) {
            outputInfo( "Can't find item " + oli.get().getItemId() + " referenced by oli " + oli.get().getId(), textResponse, logger );
            return;
        }

        String headerFormat = "%-26s  %8s %8s %9s %9s %-7s %-3s";
        String lineFormat = "%-26s  %8.2f %8.2f %9s %9s %-7s %-3s";

        if ( level == 0 ) {
            report = String.format( headerFormat, "Item", "Ordered", "Assigned", "Start", "Complete", "State", "Type" );
            if ( textResponse.getData().size() < MAX_REPORT_LINES ) logger.info( report );
            textResponse.getData().add( new Text( report ) );
        }

        report = String.format( lineFormat, Common.spacesForLevel( level) + item.getSummaryId(), oli.get().getQuantityOrdered(), oli.get().getQuantityAssigned(),
                oli.get().getStartDate(), oli.get().getCompleteDate(), oli.get().getOrderState(), oli.get().getOrderType() );
        textResponse.getData().add( new Text( report ) );

        List<OrderLineItem> childOrderLineItems = orderLineItemRepository.findByParentOliId( oli.get().getId());
        for ( OrderLineItem childOrderLineItem : childOrderLineItems ) {
            generateRecursiveOrderReport( childOrderLineItem.getId(), textResponse, level+1 );
        }


    }


    public TextResponse orderReport(long orderId) {
        TextResponse textResponse = new TextResponse();
        if (orderId == OrderLineItem_AllOrders ) {
            List<OrderLineItem> reportList = orderLineItemRepository.getOliOrderByItemIdAndCompleteDate();

            logger.info(OrderLineItem.header);
            for (com.inman.entity.OrderLineItem orderLineItem : reportList) {
                if ( textResponse.getData().size() < MAX_REPORT_LINES )  logger.info(orderLineItem.toString());
                textResponse.getData().add(new Text(orderLineItem.toString()));
            }
        } else {
            generateRecursiveOrderReport( orderId, textResponse, 0  );
        }

        if ( textResponse.getData().size() >= MAX_REPORT_LINES ) {
            logger.info( "Excess lines {} truncated at console.  " , textResponse.getData().size() - MAX_REPORT_LINES );
        }

        return textResponse;
    }
}
