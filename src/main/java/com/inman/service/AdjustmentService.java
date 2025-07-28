package com.inman.service;

import com.inman.controller.Utility;
import com.inman.entity.Adjustment;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.model.request.AdjustmentCrudRequest;
import com.inman.model.request.BomPresentSearchRequest;
import com.inman.model.response.AdjustmentCrudResponse;
import com.inman.model.response.BomResponse;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.AdjustmentRepository;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.BomRepository;
import com.inman.repository.ItemRepository;
import enums.AdjustmentType;
import enums.CrudAction;
import enums.OrderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;

import static com.inman.controller.Utility.*;

@Service
public class AdjustmentService {

    static Logger logger = LoggerFactory.getLogger( com.inman.controller.AdjustmentController.class );

    ItemRepository itemRepository;
    AdjustmentRepository adjustmentRepository;


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

        item.setQuantityOnHand( item.getQuantityOnHand() + adjustment.getAmount() );
        itemRepository.save( item );
        adjustmentRepository.save( adjustment );
        logger.info( "Item balance updated:  " + item, response, logger );
    }
}
