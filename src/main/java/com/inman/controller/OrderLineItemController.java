package com.inman.controller;

import com.inman.business.OrderLineItemService;
import com.inman.model.request.GenericSingleId;
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

    static Logger logger = LoggerFactory.getLogger("controller: " + OrderLineItemController.class);

    private OrderLineItemService orderLineItemService;

//    @Autowired
//    ItemRepository itemRepository;
//
//    public ItemCrudBatchResponse go( ItemCrudBatch itemCrudBatch) {
//        ItemCrudBatchResponse itemCrudBatchResponse = new ItemCrudBatchResponse();
//
//        for (Item itemCrudToBeCrud : itemCrudBatch.updatedRows()) {
//
//            logger.info("{} on {}", itemCrudToBeCrud.getActivityState(), itemCrudToBeCrud);
//
//            if (itemCrudToBeCrud.getActivityState() == ActivityState.INSERT) {
//                insertItem(itemCrudToBeCrud, itemCrudBatchResponse);
//            } else if (itemCrudToBeCrud.getActivityState() == ActivityState.DELETE ||
//                       itemCrudToBeCrud.getActivityState() == ActivityState.DELETE_SILENT) {
//                deleteItem(itemCrudToBeCrud, itemCrudBatchResponse);
//            } else if ( itemCrudToBeCrud.getActivityState() == ActivityState.CHANGE ) {
//                changeItem(itemCrudToBeCrud, itemCrudBatchResponse);
//            } else {
//                logger.info("Item {} was ignored because ActivityState was {}", itemCrudToBeCrud.getSummaryId(), itemCrudToBeCrud.getActivityState());
//            }
//        }
//        return itemCrudBatchResponse;
//    }
//
//    private void insertItem(Item itemCrudToBeCrud,
//                            ItemCrudBatchResponse itemCrudBatchResponse) {
//        String message;
//        Item itemToBeInserted = new Item(itemCrudToBeCrud.getSummaryId(), itemCrudToBeCrud.getDescription(),
//                itemCrudToBeCrud.getUnitCost(), itemCrudToBeCrud.getSourcing(), itemCrudToBeCrud.getLeadTime(), itemCrudToBeCrud.getMaxDepth() );
//
//
//        try {
//            itemRepository.save(itemToBeInserted);
//            var refreshedItem = itemRepository.findBySummaryId(itemToBeInserted.getSummaryId());
//            if (refreshedItem == null) {
//                message = "Item " + itemToBeInserted.getSummaryId() + " cant be re-retrieved after seemingly successful insert.";
//                itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
//                logger.error(message);
//            }
//            itemCrudBatchResponse.getData().add( refreshedItem );
//        } catch (Exception exception) {
//            var error = translateExceptionToError(exception, itemCrudToBeCrud);
//            itemCrudBatchResponse.addError(error);
//        }
//    }
//private void changeItem(Item itemCrudToBeCrud,
//                        ItemCrudBatchResponse itemCrudBatchResponse) {
//    String message;
//     try {
//        var itemToBeModified = itemRepository.findBySummaryId(itemCrudToBeCrud.getSummaryId());
//        if (itemToBeModified == null) {
//            message = "Item " + itemCrudToBeCrud.getSummaryId() + " not found.";
//            itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
//            logger.error(message);
//        } else {
//
//            itemRepository.save(itemToBeModified);
//
//            itemCrudBatchResponse.getData().add( itemToBeModified);
//        }
//        } catch(Exception exception){
//            var error = translateExceptionToError(exception, itemCrudToBeCrud);
//            itemCrudBatchResponse.addError(error);
//        }
//    }
//
//
//    private void deleteItem(Item itemCrudToBeCrud,
//                            ItemCrudBatchResponse itemCrudBatchResponse) {
//        try {
//            Item toBeDeletedItem = itemRepository.findBySummaryId(itemCrudToBeCrud.getSummaryId());
//            if (toBeDeletedItem == null) {
//
//                 if ( itemCrudToBeCrud.getActivityState() == ActivityState.DELETE) {
//                    var message = "Unable to find item " + itemCrudToBeCrud.getSummaryId() + " in database.";
//                    itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
//                    logger.info(message);
//                 } else {
//                     var message = "Silent delete on " + itemCrudToBeCrud.getSummaryId() + " in database.";
//                     itemCrudBatchResponse.getData().add( itemCrudToBeCrud );
//                     logger.info(message);
//                 }
//            } else {
//                itemRepository.deleteById(toBeDeletedItem.getId());
//                itemCrudBatchResponse.getData().add( toBeDeletedItem );
//            }
//        } catch (Exception exception) {
//            var error = translateExceptionToError(exception, itemCrudToBeCrud);
//            itemCrudBatchResponse.addError(error);
//        }
//    }
//
//    private String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
//        var detailedMessage = dataIntegrityViolationException.getMessage();
//        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
//            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
//        }
//        return detailedMessage;
//    }
//
//    ErrorLine translateExceptionToError(Exception exception, Item itemCrudSingle) {
//        String message;
//
//        if (exception instanceof DataIntegrityViolationException) {
//            message = itemCrudSingle.getActivityState() + " failed on " + itemCrudSingle.getSummaryId() + ":" +
//                    itemCrudSingle.getDescription() + " due to " +
//                    generateErrorMessageFrom((DataIntegrityViolationException) exception);
//            logger.error(message);
//            return new ErrorLine(1, message);
//        }
//        message = itemCrudSingle.getActivityState() + " failed on " + itemCrudSingle.getSummaryId() + ":" +
//                itemCrudSingle.getDescription() + " due to " +
//                exception.getMessage();
//        logger.error(message);
//        return new ErrorLine(1, message);
//    }

    @CrossOrigin
    @RequestMapping(value = OrderLineItem_ShowAll, method = RequestMethod.GET)
    public ResponseEntity<?> OrderLineItem_ShowAll( ) {
        TextResponse textResponse = new TextResponse();
        GenericSingleId gsi = new GenericSingleId();
        gsi.setIdToSearchFor(1L);
        if ( gsi.getIdToSearchFor() == OrderLineItem_AllOrders) {
            textResponse = orderLineItemService.orderReport( OrderLineItem_AllOrders  );
        }

        textResponse.setResponseType(ResponseType.MULTILINE );
        if (textResponse.getData().isEmpty()) {
            var message = "No items were processed, either due to errors or no actionable inputs.";
            logger.info(message);
            textResponse.getErrors().add(new ErrorLine(1, message));
        }
        return ResponseEntity.ok().body(textResponse);
    }

    @Autowired
    public OrderLineItemController(    OrderLineItemService orderLineItemService ) {
        this.orderLineItemService = orderLineItemService;
    }

}

