package com.inman.controller;

import com.inman.business.OrderLineItemService;
import com.inman.entity.OrderLineItem;
import com.inman.model.request.*;
import com.inman.model.response.ItemCrudBatchResponse;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.model.response.TextResponse;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Configuration
@RestController
public class OrderLineItemController {
    public static final String OrderLineItem_curd = "oli/crud";
    public static final String OrderLineItem_ShowAll = "oli/showAll";
    public static final int OrderLineItem_AllOrders = -1;
    private static final String OrderLineItem_crud = "oli/crud";

    static Logger logger = LoggerFactory.getLogger("controller: " + OrderLineItemController.class);

    private OrderLineItemService orderLineItemService;


    @CrossOrigin
    @RequestMapping(value = OrderLineItem_ShowAll, method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json" )
    public ResponseEntity<?> OrderLineItem_ShowAll( @RequestBody GenericSingleId genericSingleId  ) {
        TextResponse textResponse = orderLineItemService.orderReport(genericSingleId.getIdToSearchFor()  );

        textResponse.setResponseType(ResponseType.MULTILINE );
        if (textResponse.getData().isEmpty()) {
            var message = "No items were processed, either due to errors or no actionable inputs.";
            logger.info(message);
            textResponse.getErrors().add(new ErrorLine(1, message));
        }
        return ResponseEntity.ok().body(textResponse);
    }


    @CrossOrigin
    @RequestMapping(value = OrderLineItem_crud, method = RequestMethod.POST)
    public ResponseEntity<?> orderLineItemCrud(@RequestBody OrderLineItemRequest crudBatch ) {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        try {
             responsePackage= orderLineItemService.applyCrud( crudBatch, responsePackage  );
            responsePackage.setResponseType(ResponseType.MULTILINE );
            if (responsePackage.getData().isEmpty()) {
                var message = "No items were processed, either due to errors or no actionable inputs.";
                logger.info(message);
                responsePackage.getErrors().add(new ErrorLine(1, message));
            }
            return ResponseEntity.ok().body(responsePackage);

        } catch ( Exception exception ) {
            logger.info( "Encountered runtime exception, check response message for details.");
        }
        return ResponseEntity.badRequest().body( responsePackage );
    }

    @Autowired
    public OrderLineItemController(    OrderLineItemService orderLineItemService ) {
        this.orderLineItemService = orderLineItemService;
    }

}

