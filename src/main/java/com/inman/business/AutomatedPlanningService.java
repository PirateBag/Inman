package com.inman.business;

import com.inman.entity.*;
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
                                    OrderLineItemRepository orderLineItemRepository) {
        this.ddlRepository = ddlRepository;
        this.bomLogicService = bomLogicService;
        this.itemRepository = itemRepository;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    @Transactional
    public void basic( String parameters, TextResponse textResponse ) {

        ArrayList<OrderLineItem> newOrders = new ArrayList<>();

        calculateMaxDepths(textResponse);

        List<Item> itemsToBePlanned = itemRepository.findAllByOrderByMaxDepthAsc();

        int temporaryStartingOrder = 21;

        for (Item item : itemsToBePlanned) {
            logger.info("Item: {}", item);

            List<OrderLineItem> orders = orderLineItemRepository.findByItemIdAndOrderStateOrderByCompleteDate( item.getId(), OrderState.OPEN );

            if ( orders.isEmpty()) {
                logger.info( "No outstanding orders for " + item.getSummaryId() );
                continue;
            }

            double balance = item.getQuantityOnHand();
            logger.info( "    " + OrderLineItem.header + "  Balance"  );
            for ( OrderLineItem order : orders ) {
                if ( order.getParentOliId() == 0 ) {
                    logger.info( String.format( "    Order %d has no parent. ", order.getId()  ) );
                    break;
                }

                balance += order.getEffectiveQuantityOrdered();
                logger.info( String.format( "    %s : %f8.2", order, balance ) );

                if ( balance < 0 ) {
                    OrderLineItem newOrder = new OrderLineItem(order);
                    newOrder.setId(temporaryStartingOrder++);
                    newOrder.setQuantityOrdered(max(item.getMinimumOrderQuantity(), abs( balance) ));
                    newOrder.setActivityState(ActivityState.INSERT);
                    newOrder.setStartDate(null);
                    newOrder.setCompleteDate(order.getStartDate());
                    newOrder.setOrderType(item.getSourcing().equals(Item.SOURCE_MAN) ? OrderType.MOHEAD : OrderType.PO);
                    balance += newOrder.getQuantityOrdered();
                    logger.info( String.format("    Ordering %8.2f more", newOrder.getQuantityOrdered() ) );
                    newOrders.add(newOrder);
                }
            }
        }

        for ( OrderLineItem newOrder : newOrders ) {
            outputInfo( "New order: " + newOrder , textResponse );
        }
        logger.info("apBasic loop exited with " + textResponse.getErrors().size() + " errors");
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

}
