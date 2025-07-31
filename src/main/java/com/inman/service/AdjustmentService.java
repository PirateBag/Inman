package com.inman.service;

import com.inman.controller.Messages;
import com.inman.controller.Utility;
import com.inman.entity.Adjustment;
import com.inman.entity.Item;
import com.inman.model.request.AdjustmentCrudRequest;
import com.inman.model.response.AdjustmentCrudResponse;
import com.inman.model.response.TextResponse;
import com.inman.repository.AdjustmentRepository;
import com.inman.repository.ItemRepository;
import enums.AdjustmentType;
import enums.OrderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static com.inman.controller.Messages.NO_DATA_TO_REPORT;
import static com.inman.controller.Utility.*;

@Service
public class AdjustmentService {

    static Logger logger = LoggerFactory.getLogger( com.inman.controller.AdjustmentController.class );

    ItemRepository itemRepository;
    AdjustmentRepository adjustmentRepository;

    public static final String EXTENDED_HEADER_FORMAT =       "%8s    %-6s %-6s  %-4s  %9s  %4s    %10s";
    public static final String EXTENDED_LINE_FORMAT =         "%8.2f  %6d  %6d   %-4s  %9s  %4s    %10s ";
    public static final String EXTENDED_OVER_HEADER =                                  "  --------             Adjustment        -------       --  Item  --";
    public static final String EXTENDED_HEADER = String.format(EXTENDED_HEADER_FORMAT, "Amount", "Item", "Order", "Type", "Date", "AdTp", "Summary" );


    @Autowired
    public AdjustmentService( ItemRepository itemRepository, AdjustmentRepository adjustmentRepository ) {
        this.itemRepository = itemRepository;
        this.adjustmentRepository = adjustmentRepository;
    }

    @Transactional
    public AdjustmentCrudResponse crud ( AdjustmentCrudRequest adjustmentCrudRequest, AdjustmentCrudResponse response ) {

        int lineNumber = 0;

        for ( Adjustment adjustment : adjustmentCrudRequest.getRows() ) {
            logger.info("Adjustment {} ", adjustment.toString() );

            switch ( adjustment.getCrudAction() ) {
                case INSERT ->  insert( adjustment, response, lineNumber);
                default -> {
                          Utility.outputErrorAndThrow("Crud Action " + adjustment.getCrudAction() + " not supported",
                            response, logger);
                }
            }
            lineNumber++;
        }
        logger.info("Update loop exited with " + response.getErrors().size() + " errors");
        if ( !response.getErrors().isEmpty()) {
            outputErrorAndThrow( "At least one error occurred", response, logger  );
        }
        return response;
    }

    private void insert ( Adjustment adjustment, AdjustmentCrudResponse response , int lineNumber ) {
        adjustment.validate();

        Item item = itemRepository.findById( adjustment.getItemId() );
        if ( item == null ) {
            outputErrorAndThrow("Item " + adjustment.getItemId() + " not found", response, logger);
        }
        if ( adjustment.getAdjustmentType() != AdjustmentType.ITEM ) {
            outputErrorAndThrow("Adjustment type " + adjustment.getAdjustmentType() + " not supported", response, logger);
        }

        if ( adjustment.getOrderType() != OrderType.NA ) {
            outputErrorAndThrow("Order type " + adjustment.getOrderType() + " not supported", response, logger);
        }

        assert item != null;
        item.setQuantityOnHand( item.getQuantityOnHand() + adjustment.getAmount() );
        itemRepository.save( item );
        adjustmentRepository.save( adjustment );
        logger.info( "Item balance updated:  " + item, response, logger );
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
            // "Amount", "Item", "Order", "Type", "Date", "AdTp", "Summary"
            textResponse.addText( EXTENDED_LINE_FORMAT.formatted(
                    adjustment.getAmount(), adjustment.getItemId(), adjustment.getOrderId(),
                    adjustment.getOrderType(),
                    adjustment.getEffectiveDate(), adjustment.getAdjustmentType(), "W-666" ), Optional.of( logger ));
        }
    }
}
