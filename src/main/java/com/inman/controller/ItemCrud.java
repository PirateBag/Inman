package com.inman.controller;

import com.inman.entity.ActivityState;
import com.inman.entity.Item;
import com.inman.model.request.ItemCrudBatch;
import com.inman.model.request.ItemCrudSingle;
import com.inman.model.response.ItemCrudBatchResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Configuration
@RestController
public class ItemCrud {
    public static final String UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION = "Unique index or primary key violation";
    public static final String ItemCrudRequestURL = "item/crud";

    static Logger logger = LoggerFactory.getLogger("controller: " + ItemCrud.class);

    @Autowired
    ItemRepository itemRepository;

    public ItemCrudBatchResponse go( ItemCrudBatch itemCrudBatch) {
        ItemCrudBatchResponse itemCrudBatchResponse = new ItemCrudBatchResponse();

        for (ItemCrudSingle itemCrudToBeCrud : itemCrudBatch.updatedRows()) {

            logger.info("{} on {}", itemCrudToBeCrud.getActivityState(), itemCrudToBeCrud);

            if (itemCrudToBeCrud.getActivityState() == ActivityState.INSERT) {
                insertItem(itemCrudToBeCrud, itemCrudBatchResponse);
            } else if (itemCrudToBeCrud.getActivityState() == ActivityState.DELETE ||
                       itemCrudToBeCrud.getActivityState() == ActivityState.DELETE_SILENT) {
                deleteItem(itemCrudToBeCrud, itemCrudBatchResponse);
            } else if ( itemCrudToBeCrud.getActivityState() == ActivityState.CHANGE ) {
                changeItem(itemCrudToBeCrud, itemCrudBatchResponse);
            } else {
                logger.info("Item {} was ignored because ActivityState was {}", itemCrudToBeCrud.getSummaryId(), itemCrudToBeCrud.getActivityState());
            }
        }
        return itemCrudBatchResponse;
    }

    private void insertItem(ItemCrudSingle itemCrudToBeCrud,
                            ItemCrudBatchResponse itemCrudBatchResponse) {
        String message;
        Item itemToBeInserted = new Item(itemCrudToBeCrud.getSummaryId(), itemCrudToBeCrud.getDescription(), itemCrudToBeCrud.getUnitCost(), itemCrudToBeCrud.getSourcing());
        try {
            itemRepository.save(itemToBeInserted);
            var refreshedItem = itemRepository.findBySummaryId(itemToBeInserted.getSummaryId());
            if (refreshedItem == null) {
                message = "Item " + itemToBeInserted.getSummaryId() + " cant be re-retrieved after seemingly successful insert.";
                itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
                logger.error(message);
            }
            itemCrudBatchResponse.getData().add(new ItemCrudSingle(refreshedItem.getSummaryId(), refreshedItem.getDescription(),
                    refreshedItem.getUnitCost(), refreshedItem.getSourcing(), ActivityState.INSERT));
        } catch (Exception exception) {
            var error = translateExceptionToError(exception, itemCrudToBeCrud);
            itemCrudBatchResponse.addError(error);
        }
    }
private void changeItem(ItemCrudSingle itemCrudToBeCrud,
                        ItemCrudBatchResponse itemCrudBatchResponse) {
    String message;
     try {
        var itemToBeModified = itemRepository.findBySummaryId(itemCrudToBeCrud.getSummaryId());
        if (itemToBeModified == null) {
            message = "Item " + itemCrudToBeCrud.getSummaryId() + " not found.";
            itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
            logger.error(message);
        } else {
            var crudItem = itemCrudToBeCrud.generateItem( itemToBeModified.getId() );
            itemRepository.save(crudItem);

            itemCrudBatchResponse.getData().add(new ItemCrudSingle(itemToBeModified.getSummaryId(), itemToBeModified.getDescription(),
                    itemToBeModified.getUnitCost(), itemToBeModified.getSourcing(), ActivityState.CHANGE));
        }
        } catch(Exception exception){
            var error = translateExceptionToError(exception, itemCrudToBeCrud);
            itemCrudBatchResponse.addError(error);
        }
    }


    private void deleteItem(ItemCrudSingle itemCrudToBeCrud,
                            ItemCrudBatchResponse itemCrudBatchResponse) {
        try {
            Item toBeDeletedItem = itemRepository.findBySummaryId(itemCrudToBeCrud.getSummaryId());
            if (toBeDeletedItem == null) {

                 if ( itemCrudToBeCrud.getActivityState() == ActivityState.DELETE) {
                    var message = "Unable to find item " + itemCrudToBeCrud.getSummaryId() + " in database.";
                    itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
                    logger.info(message);
                 } else {
                     var message = "Silent delete on " + itemCrudToBeCrud.getSummaryId() + " in database.";
                     itemCrudBatchResponse.getData().add(
                             new ItemCrudSingle(itemCrudToBeCrud.getSummaryId(), itemCrudToBeCrud.getDescription(),
                                     itemCrudToBeCrud.getUnitCost(), itemCrudToBeCrud.getSourcing(), itemCrudToBeCrud.getActivityState() ) );
                     logger.info(message);
                 }
            } else {
                itemRepository.deleteById(toBeDeletedItem.getId());
                itemCrudBatchResponse.getData().add(
                        new ItemCrudSingle(toBeDeletedItem.getSummaryId(), toBeDeletedItem.getDescription(),
                                toBeDeletedItem.getUnitCost(), toBeDeletedItem.getSourcing(), itemCrudToBeCrud.getActivityState()));
            }
        } catch (Exception exception) {
            var error = translateExceptionToError(exception, itemCrudToBeCrud);
            itemCrudBatchResponse.addError(error);
        }
    }

    private String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
        var detailedMessage = dataIntegrityViolationException.getMessage();
        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
        }
        return detailedMessage;
    }

    ErrorLine translateExceptionToError(Exception exception, ItemCrudSingle itemCrudSingle) {
        String message;

        if (exception instanceof DataIntegrityViolationException) {
            message = itemCrudSingle.getActivityState() + " failed on " + itemCrudSingle.getSummaryId() + ":" +
                    itemCrudSingle.getDescription() + " due to " +
                    generateErrorMessageFrom((DataIntegrityViolationException) exception);
            logger.error(message);
            return new ErrorLine(1, message);
        }
        message = itemCrudSingle.getActivityState() + " failed on " + itemCrudSingle.getSummaryId() + ":" +
                itemCrudSingle.getDescription() + " due to " +
                exception.getMessage();
        logger.error(message);
        return new ErrorLine(1, message);
    }

    @CrossOrigin
    @RequestMapping(value = ItemCrudRequestURL, method = RequestMethod.POST)
    public ResponseEntity<?> itemCrudRequest(@RequestBody ItemCrudBatch itemCrudBatch) {
        ItemCrudBatchResponse itemCrudBatchResponse = go( itemCrudBatch );
        itemCrudBatchResponse.setResponseType(ResponseType.MULTILINE );
        if (itemCrudBatchResponse.getData().isEmpty()) {
            var message = "No items were processed, either due to errors or no actionable inputs.";
            logger.info(message);
            itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
        }
        return ResponseEntity.ok().body(itemCrudBatchResponse);
    }

    public ItemCrud( @Autowired ItemRepository itemRepository ) {
        this.itemRepository = itemRepository;
    }
}

