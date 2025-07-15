package com.inman.business;

import com.inman.entity.*;
import com.inman.model.request.GenericSingleId;
import com.inman.model.request.OrderLineItemRequest;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.TextResponse;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Double.max;
import static java.lang.Math.abs;

@Service
public class AutomatedPlanningService {

    static Logger logger = LoggerFactory.getLogger( AutomatedPlanningService.class );
    private final ItemRepository itemRepository;
    private final DdlRepository ddlRepository;
    private final BomLogicService bomLogicService;
    private final OrderLineItemRepository orderLineItemRepository;
    private final OrderLineItemService  orderLineItemService;

    private void outputInfo(String message, ResponsePackage<?> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
    }

    private void outputErrorAndThrow(String message, ResponsePackage<?> responsePackage) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
        throw new RuntimeException(message);
    }

    @Autowired
    public AutomatedPlanningService(DdlRepository ddlRepository,
                                    BomLogicService bomLogicService, ItemRepository itemRepository,
                                    OrderLineItemRepository orderLineItemRepository,
                                    OrderLineItemService orderLineItemService ) {
        this.ddlRepository = ddlRepository;
        this.bomLogicService = bomLogicService;
        this.itemRepository = itemRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.orderLineItemService = orderLineItemService;
    }

    @Transactional
    public void basic( String parameters, TextResponse textResponse ) {

        ArrayList<OrderLineItem> newOrders = new ArrayList<>();

        calculateMaxDepths(textResponse);

        List<Item> itemsToBePlanned = itemRepository.findAllByOrderByMaxDepthAsc();

        for (Item item : itemsToBePlanned) {
            List<OrderLineItem> orders = orderLineItemRepository.findByItemIdAndOrderStateOrderByCompleteDate( item.getId(), OrderState.OPEN );

            logger.info("Item: {} has {} orders", item, orders.size());

            double balance = item.getQuantityOnHand();
            textResponse.addText( "Inventory Analysis for " + item, Optional.of( logger )  );
            textResponse.addText( "  Opening Balance of " + balance +
                    " and there are " + orders.size() + " open orders", Optional.of( logger )  );

            for ( OrderLineItem order : orders ) {
                if ( order.getParentOliId() == 0 ) {
                    logger.info( String.format( "  Order %d has no parent. ", order.getId()  ) );
                    break;
                }

                balance += order.getEffectiveQuantityOrdered();
                textResponse.addText( String.format( "  %s : %f8.2", order, balance ), Optional.of( logger ) );

                if ( balance < 0 ) {
                    OrderLineItem newOrder = new OrderLineItem(order);
                    newOrder.setQuantityOrdered(max(item.getMinimumOrderQuantity(), abs( balance) ));
                    newOrder.setActivityState(ActivityState.INSERT);
                    newOrder.setStartDate(null);
                    newOrder.setCompleteDate(order.getStartDate());
                    newOrder.setOrderType(item.getSourcing().equals(Item.SOURCE_MAN) ? OrderType.MOHEAD : OrderType.PO);
                    balance += newOrder.getQuantityOrdered();
                    textResponse.addText( " +" + newOrder, Optional.of( logger ) );
                    newOrders.add(newOrder);
                }
            }
            if (!newOrders.isEmpty()) {
                textResponse.addText("    Projected balance of " + balance + " on " + newOrders.getLast().getCompleteDate(),
                        Optional.of(logger));
                applyProposedChanges( newOrders );
            }
        }
     }

    public void inventoryBalanceForItem( Item item, TextResponse textResponse  ) {

        List<OrderLineItem> orders = orderLineItemRepository.findByItemIdAndOrderStateOrderByCompleteDate( item.getId(), OrderState.OPEN );
        double balance = item.getQuantityOnHand();
        textResponse.addText( "Inventory Analysis for " + item, Optional.of( logger )  );
        textResponse.addText( "Opening Balance of " + balance +
                " and there are " + orders.size() + " open orders", Optional.of( logger )  );
        textResponse.addText( OrderLineItem.header + "  Balance", Optional.of( logger ) );

        for ( OrderLineItem order : orders ) {
            balance += order.getEffectiveQuantityOrdered();
            textResponse.addText( String.format( "%s %8.2f", order.toStringWithSignedQuantity(), balance ), Optional.of( logger ) );
        }
    }


    private void applyProposedChanges( List<OrderLineItem> orders ) {

        OrderLineItemRequest crudBatch = new OrderLineItemRequest(orders.toArray(new OrderLineItem[0]));
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();

        orderLineItemService.applyCrud(crudBatch, responsePackage);

        logger.info( "errors reported when trying to create orders: ");
        for( ErrorLine error : responsePackage.getErrors() ) { logger.info( error.getMessage() ); }
//
//        for ( OrderLineItem newOrder : newOrders ) {
//            outputInfo( "New order: " + newOrder , textResponse );
//        }
        }

        private void calculateMaxDepths(TextResponse textResponse) {
        outputInfo( "Resetting max depth...", textResponse);

        var numberOfResets = ddlRepository.resetMaxDepth();

        ArrayList<Text> texts = new ArrayList<>();
        textResponse.addText( numberOfResets + " items were reset", Optional.of( logger ) );

        List<Item> items = itemRepository.findAll();

        for (Item item : items) {
            logger.info("Processing item {}:{}", item.getId(), item.getSummaryId());
            bomLogicService.updateMaxDepthOf( item.getId(), texts );
        }
    }

    public void inventoryBalanceProjection(GenericSingleId genericSingleId, TextResponse textResponse) {

        Optional<Item> item = itemRepository.findById( genericSingleId.getIdToSearchFor() );

        if (item.isEmpty()) {
            outputErrorAndThrow( "Item not found", textResponse  );
        }
        inventoryBalanceForItem(item.get(), textResponse  );
    }

}
