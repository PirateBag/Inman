package com.inman.service;

import com.inman.controller.Application;
import com.inman.controller.Utility;
import com.inman.entity.*;
import com.inman.model.request.OrderLineItemRequest;
import com.inman.model.response.*;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
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

import static com.inman.service.ReflectionHelpers.compareObjects;
import static com.inman.controller.OrderLineItemController.OrderLineItem_AllOrders;
import static com.inman.controller.Utility.*;

@Service
public class OrderLineItemService {
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

    private void outputInfo(String message, ResponsePackage<?> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
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
            outputErrorAndThrow(message, oliResponse);
        } catch (RuntimeException runtimeException) {
            outputErrorAndThrow( "Unable to insert " + orderLineItem + ":" + runtimeException.getMessage(), oliResponse);
        }

    }

    /**
     * Add orderLineItems to the parent owner based on the BOM of the item associated with the parent.
     *
     * @param parentOli parent of the newly inserted order.k
     * @param oliResponse final response message, modified by side effect.
     *
     */
    private void addLineItemsToOrder( OrderLineItem parentOli , ResponsePackage<OrderLineItem> oliResponse) {
        Optional<Item> item = Optional.ofNullable(itemRepository.findById(parentOli.getItemId()));

        if ( item.isEmpty() ) {
            outputErrorAndThrow( "Unable to find " + parentOli.getItemId(), oliResponse );
        }

        BomPresent[] childrenOfItem = bomPresentRepository.findByParentId( parentOli.getItemId() );
        int count = 1;
        for ( BomPresent bomPresent : childrenOfItem ) {
            OrderLineItem oli = new OrderLineItem();
            oli.setItemId( bomPresent.getChildId() );
            oli.setQuantityOrdered( parentOli.getQuantityOrdered() * bomPresent.getQuantityPer() );

            oli.setCompleteDate( parentOli.getStartDate() );

            LocalDate derviedStart = LocalDate.parse( parentOli.getStartDate(), DATE_FORMATTER).minusDays(item.get().getLeadTime());
            oli.setStartDate( derviedStart.format(DATE_FORMATTER));
            oli.setParentOliId( parentOli.getId() );
            oli.setQuantityAssigned(  0.0 );
            oli.setActivityState( parentOli.getActivityState() );
            oli.setOrderState( parentOli.getOrderState() );
            oli.setOrderType( item.get().getSourcing().equals( Item.SOURCE_MAN ) ? OrderType.MODET : OrderType.PO );
            var updatedOli = orderLineItemRepository.save(oli);
            logger.info( updatedOli.toString()  );
            count++;
        }
        outputInfo( "Added " + count + " lines to order " + parentOli.getId()  , oliResponse);
    }

    private void delete(OrderLineItem orderLineItem, ResponsePackage<OrderLineItem> oliResponse) {
        String message;
        try {
            Optional<OrderLineItem> orderLineItemFromRepository = orderLineItemRepository.findById( orderLineItem.getId());
            if (orderLineItemFromRepository.isPresent()) {
                orderLineItemRepository.delete(orderLineItemFromRepository.get());
            } else {
                outputErrorAndThrow("Unable to find " + orderLineItem, oliResponse);
            }

            if ( orderLineItem.getOrderState() == OrderState.OPEN ) {
                outputErrorAndThrow( "Delete on open order is prohibited.", oliResponse );
            }

            List<OrderLineItem> childrenOfOrder = orderLineItemRepository.findByParentOliId( orderLineItem.getId() );
            if (!childrenOfOrder.isEmpty()) {
                outputErrorAndThrow( "Deletion on " + orderLineItem + " failed because it has children.", oliResponse);
            }

            orderLineItemFromRepository.get().setActivityState(orderLineItem.getActivityState());
            oliResponse.getData().add(orderLineItemFromRepository.get());
            orderLineItemRepository.deleteById( orderLineItem.getId());

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to " + orderLineItem.getActivityState() + " " + orderLineItem + ":" +
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
            message = "Unable to " + orderLineItem.getActivityState() + " " + orderLineItem + ":" +
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
     * @param oldOli
     * @param newOli
     * @param oliResponse
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
            outputInfo( "Removed " + count + " lines from order " + newOli, oliResponse );
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
       outputInfo( "Updated " + count + " line items to state " + newOli.getOrderState(), oliResponse );
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
            outputInfo( "Order Id is different",  oliResponse );
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
        if (Application.isTestName("0719_oliCrud")) {
            logger.info("You have arrived at " + Application.getTestName());
    }

        for (OrderLineItem orderLineItem : crudBatch.rows()) {
            logger.info("{} on {}", orderLineItem.getActivityState(), orderLineItem);

            if (orderLineItem.getActivityState() == ActivityState.INSERT) {
                insert(orderLineItem, responsePackage );
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

        String headerFormat = "%-26s  %8s %8s %9s %9s %-7s %-3s";
        String lineFormat = "%-26s  %8.2f %8.2f %9s %9s %-7s %-3s";

        if ( level == 0 ) {
            report = String.format( headerFormat, "Item", "Ordered", "Assigned", "Start", "Complete", "State", "Type" );
            logger.info( report );
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
                logger.info(orderLineItem.toString());
                textResponse.getData().add(new Text(orderLineItem.toString()));
            }
        } else {
            generateRecursiveOrderReport( orderId, textResponse, 0  );
        }
        return textResponse;
    }
}
