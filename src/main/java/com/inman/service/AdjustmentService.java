package com.inman.service;

import com.inman.controller.Messages;
import com.inman.controller.Utility;
import com.inman.entity.Adjustment;
import com.inman.entity.Item;
import com.inman.entity.OrderLineItem;
import com.inman.model.request.AdjustmentCrudRequest;
import com.inman.model.response.AdjustmentCrudResponse;
import com.inman.model.response.TextResponse;
import com.inman.repository.AdjustmentRepository;
import com.inman.repository.ItemRepository;
import com.inman.repository.OrderLineItemRepository;
import enums.AdjustmentType;
import enums.OrderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.inman.controller.Messages.*;
import static com.inman.controller.Utility.*;
import static java.lang.Math.round;

@Service
public class AdjustmentService {

    static Logger logger = LoggerFactory.getLogger( "AdjustmentService"  );

    private final ItemRepository itemRepository;
    private final AdjustmentRepository adjustmentRepository;
    private final OrderLineItemRepository orderLineItemRepository;

    public static final String EXTENDED_HEADER_FORMAT =       "%8s    %-6s %-6s  %-4s  %9s  %4s    %10s  %9s";
    public static final String EXTENDED_LINE_FORMAT =         "%8.2f  %6d  %6d   %-4s  %9s  %4s    %10s  %9.2f";
    public static final String EXTENDED_OVER_HEADER =                                  "  --------             Adjustment        -------       ----  Item  ----";
    public static final String EXTENDED_HEADER = String.format(EXTENDED_HEADER_FORMAT, "Amount", "Item", "Order", "Type", "Date", "AdTp", "Summary", "Balance" );


    @Autowired
    public AdjustmentService(ItemRepository itemRepository, AdjustmentRepository adjustmentRepository, OrderLineItemRepository orderLineItemRepository) {
        this.itemRepository = itemRepository;
        this.adjustmentRepository = adjustmentRepository;
        this.orderLineItemRepository = orderLineItemRepository;

    }

    @Transactional
    public AdjustmentCrudResponse crud ( AdjustmentCrudRequest adjustmentCrudRequest, AdjustmentCrudResponse response ) {

        for ( Adjustment adjustment : adjustmentCrudRequest.getRows() ) {
            logger.info("Adjustment {} ", adjustment.toString() );
            switch ( adjustment.getCrudAction() ) {
                case INSERT ->  insert( adjustment, response );
                default -> {
                          Utility.outputErrorAndThrow("Crud Action " + adjustment.getCrudAction() + " not supported",
                            response, logger);
                }
            }
        }
        logger.info("Update loop exited with " + response.getErrors().size() + " errors");
        if ( !response.getErrors().isEmpty()) {
            outputErrorAndThrow( "At least one error occurred", response, logger  );
        }
        return response;
    }

    private void insert ( Adjustment adjustment, AdjustmentCrudResponse response ) {
        adjustment.validate();

        switch (adjustment.getAdjustmentType()) {
            case ITEM -> insertItemAdjustment(adjustment, response );
            case XFER -> insertOrderAdjustment(adjustment, response );
            default -> {
                Utility.outputErrorAndThrow(ILLEGAL_STATE.formatted(adjustment.getAdjustmentType()),
                        response, logger);
            }
        }
    }

    /**
     * Handle adjustements to inventory items only unrelated to orders.
     * @param adjustment
     * @param response
     * @param lineNumber
     *
     * Sideffects:  Item QuantityOnHand altered by adjustement and a new Adjustement is audited.
     */
    private void insertItemAdjustment(  Adjustment adjustment, AdjustmentCrudResponse response )
    {
        if ( adjustment.getOrderType() != OrderType.NA ) {
            outputErrorAndThrow("Order type " + adjustment.getOrderType() + " not supported", response, logger);
        }

        Item item = itemRepository.findById( adjustment.getItemId() );
        if ( item == null ) {
            outputErrorAndThrow("Item " + adjustment.getItemId() + " not found", response, logger);
        }

        assert item != null;
        item.setQuantityOnHand( item.getQuantityOnHand() + adjustment.getAmount() );
        double oldBalance = item.getQuantityOnHand();
        double newBalance = oldBalance + adjustment.getAmount();
        item.setQuantityOnHand( newBalance );
        logger.info( "Updated balance of {} from {} to {}", item.getSummaryId(), oldBalance, newBalance );
        itemRepository.save( item );
        adjustmentRepository.save( adjustment );
        logger.info( "Item balance updated:  " + item, response, logger );
    }

    /**
     * Handle adjustements to inventory balances when items are transferred to or from orders.
     * @param adjustment
     * @param response
     * @param lineNumber
     *
     * Side effects:  Item QuantityOnHand and Order QuantityAssigned updated in database.
     */
    private void insertOrderAdjustment(  Adjustment adjustment, AdjustmentCrudResponse response )
    {
c        Item item = itemRepository.findById( adjustment.getItemId() );
        if ( item == null ) {
            outputErrorAndThrow(ITEM_REF_NOT_FOUND.formatted( "Adustment",  adjustment.getItemId() ),
                     response, logger);
        }

        Optional<OrderLineItem> orderLineItem = orderLineItemRepository.findById( adjustment.getOrderId() );
        if ( orderLineItem.isEmpty() ) {
            outputErrorAndThrow(ORDER_REF_NOT_FOUND.formatted( "Adustment",  adjustment.getItemId() ),
                    response, logger);
        }

        if ( adjustment.getItemId() != orderLineItem.get().getItemId() ) {
            outputErrorAndThrow( ORDER_AND_ADJUSTMENT_DISAGREE_ITEM.formatted(
                    orderLineItem.get().getId(), orderLineItem.get().getItemId(), adjustment.getItemId() ),
                    response, logger );
        }

        if ( adjustment.getOrderType() == OrderType.NA ) {
            outputErrorAndThrow(ADJUST_ORDER_TYPE, response, logger);
        }

        double oldQuantityOnHand = item.getQuantityOnHand();
        double oldQuantityAssigned =  orderLineItem.get().getQuantityAssigned();

        double newQuantityOnHand = 0.0;
        double newQuantityAssigned = 0.0;

        /* PO's and MOHEAD's add to the quantity for an item.
        MODETs use up quantity from the item.
         */
        if ( orderLineItem.get().getOrderType() == OrderType.PO
        || orderLineItem.get().getOrderType() == OrderType.MOHEAD ) {
            newQuantityOnHand = oldQuantityOnHand + adjustment.getAmount();
            newQuantityAssigned =oldQuantityAssigned + adjustment.getAmount();

            if ( orderLineItem.get().getOrderType() == OrderType.MOHEAD ) {
                var childrenOfMoHead = orderLineItemRepository.findByParentOliId( orderLineItem.get().getId() );
                List<Adjustment> proposedAdjustments = new ArrayList<>();
                var fractionOfOrder = Utility.round( adjustment.getAmount() / orderLineItem.get().getQuantityOrdered(), 1 );
                for ( OrderLineItem child : childrenOfMoHead ) {
                    Adjustment childAdjustment = new Adjustment( child.getQuantityOrdered()*fractionOfOrder,
                            child.getItemId(), 0, child.getOrderType(), child.getCompleteDate(), AdjustmentType.XFER );
                    proposedAdjustments.add( childAdjustment );
                }

                logger.info( "There are {} children and {} proposed adjustments.", childrenOfMoHead, proposedAdjustments );
                logger.info( Adjustment.RAW_HEADDER );
                for ( Adjustment proposedAdjustment : proposedAdjustments ) {
                    logger.info( proposedAdjustment.toString() );
                }
            }



        } else if ( orderLineItem.get().getOrderType() == OrderType.MODET ){
            newQuantityOnHand = oldQuantityOnHand - adjustment.getAmount();
            newQuantityAssigned =oldQuantityAssigned + adjustment.getAmount();
        } else throw new UnsupportedOperationException( );

        logger.info( "Item QuantityOnHand adjusted from {} to {} and Order QuantityAssigned adjusted from {} to {}.",
                oldQuantityOnHand, newQuantityOnHand, oldQuantityAssigned, newQuantityAssigned );

        item.setQuantityOnHand( newQuantityOnHand );
        orderLineItem.get().setQuantityAssigned( newQuantityAssigned );
        itemRepository.save( item );
        orderLineItemRepository.save( orderLineItem.get() );
        adjustmentRepository.save( adjustment );
    }



    public void sleep(long id, TextResponse textResponse ) {
        Collection<Adjustment> adjustments = adjustmentRepository.findAll();

        if ( adjustments.isEmpty() ) {
            outputInfo( NO_DATA_TO_REPORT.formatted( "All Adjustments" ), textResponse, logger );
        }

        for ( Adjustment adjustment : adjustments ) {
            if ( textResponse.getData().isEmpty() ) {
                textResponse.addText( Adjustment.RAW_HEADDER, Optional.of( logger ) );
            }
            textResponse.addText( adjustment.toString(), Optional.of( logger ) );
        }
    }

    public void reportAll(long id, TextResponse textResponse ) {
        Collection<Adjustment> adjustments = adjustmentRepository.findAll();

        if ( adjustments.isEmpty() ) {
            outputInfo( NO_DATA_TO_REPORT.formatted( "All Adjustments" ), textResponse, logger );
        }

        for ( Adjustment adjustment : adjustments ) {
            if ( textResponse.getData().isEmpty() ) {
                textResponse.addText( EXTENDED_OVER_HEADER, Optional.of( logger ) );
                textResponse.addText( EXTENDED_HEADER, Optional.of( logger ) );
            }

            Item item = itemRepository.findById( adjustment.getItemId() );
            if ( item == null ) {
                outputErrorAndThrow(ITEM_REF_NOT_FOUND.formatted( "Adjustment", adjustment.getItemId()) ,
                        textResponse, logger);
            }


            // "Amount", "Item", "Order", "Type", "Date", "AdTp", "Summary", "Balance"
            textResponse.addText( EXTENDED_LINE_FORMAT.formatted(
                    adjustment.getAmount(), adjustment.getItemId(), adjustment.getOrderId(),
                    adjustment.getOrderType(),
                    adjustment.getEffectiveDate(), adjustment.getAdjustmentType(), item.getSummaryId(), item.getQuantityOnHand() ), Optional.of( logger ));
        }
    }
}
