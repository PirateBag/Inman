package com.inman.controller;

import com.inman.entity.Item;
import com.inman.model.request.ItemCrudBatch;
import com.inman.model.response.ItemCrudBatchResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.ItemRepository;
import com.inman.repository.ItemSpecifications;
import enums.CrudAction;
import enums.SourcingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static com.inman.controller.LoggingUtility.outputInfo;
import static com.inman.controller.Messages.*;
import static com.inman.controller.Utility.*;

@Configuration
@RestController
public class ItemCrud {
    public static final String UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION = "Unique index or primary key violation";
    public static final String ItemCrudRequestURL = "item/crud";
    public static final String ItemCrudQuery = "item/crudQuery";

    static Logger logger = LoggerFactory.getLogger("controller: " + ItemCrud.class);

    ItemRepository itemRepository;

    public void validateItemBasedOnAction( Item item, ItemCrudBatchResponse itemCrudBatchResponse ) {

        if (  item.getCrudAction() == CrudAction.CHANGE || item.getCrudAction() == CrudAction.DELETE  ) {
            if ( item.getId() < 1L && normalize( item.getDescription() ).length() < 3 ) {
                outputErrorAndThrow( String.format( ID_OR_SUMMARYID_FOR_CHANGE, item.getId(), normalize( item.getDescription())),
                        itemCrudBatchResponse, logger );
            }
        } else if ( item.getCrudAction() == CrudAction.INSERT && item.getId() != 0L ) {
            outputInfo( ID_MUST_BE_ZERO_FOR_INSERTS, itemCrudBatchResponse, logger );
            item.setId(0L);
        }

        if ( item.getCrudAction() == CrudAction.CHANGE
        || item.getCrudAction() == CrudAction.INSERT ) {
            if ( item.getDescription().length() < 3 ) {
                outputErrorAndThrow( "Item description null or too short", itemCrudBatchResponse, logger );
            }

            if ( item.getLeadTime() < 1 ) {
                outputErrorAndThrow( String.format( Messages.LEAD_TIME_LESS_THAN_1, item.getId(), item.getLeadTime() ),
                        itemCrudBatchResponse, logger  );
            }
        }

        if ( item.getCrudAction() == CrudAction.INSERT && item.getUnitCost() != 0.0 && item.getSourcing() == SourcingType.MAN  ) {
            outputErrorAndThrow(UNIT_COST_MUST_BE_ZERO, itemCrudBatchResponse, logger );
        }
    }

    public ItemCrudBatchResponse go( ItemCrudBatch itemCrudBatch, ItemCrudBatchResponse itemCrudBatchResponse ) {
        for (Item itemCrudToBeCrud : itemCrudBatch.updatedRows()) {
            logger.info("{} on {}", itemCrudToBeCrud.getCrudAction(), itemCrudToBeCrud);

            validateItemBasedOnAction(itemCrudToBeCrud, itemCrudBatchResponse);

            if (itemCrudToBeCrud.getCrudAction() == CrudAction.INSERT) {
                insertItem(itemCrudToBeCrud, itemCrudBatchResponse);
            } else if (itemCrudToBeCrud.getCrudAction() == CrudAction.DELETE ||
                       itemCrudToBeCrud.getCrudAction() == CrudAction.DELETE_SILENT) {
                deleteItem(itemCrudToBeCrud, itemCrudBatchResponse);
            } else if ( itemCrudToBeCrud.getCrudAction() == CrudAction.CHANGE ) {
                changeItem(itemCrudToBeCrud, itemCrudBatchResponse);
            } else {
                logger.info("Item {} was ignored because CrudAction was {}", itemCrudToBeCrud.getDescription(), itemCrudToBeCrud.getCrudAction());
            }
        }
        return itemCrudBatchResponse;
    }

    private void insertItem(Item itemCrudToBeCrud,
                            ItemCrudBatchResponse itemCrudBatchResponse) {
        String message;
        Item itemToBeInserted = new Item( itemCrudToBeCrud.getDescription(),
                itemCrudToBeCrud.getUnitCost(), itemCrudToBeCrud.getSourcing(), itemCrudToBeCrud.getLeadTime(), itemCrudToBeCrud.getMaxDepth(),
                itemCrudToBeCrud.getQuantityOnHand(),  itemCrudToBeCrud.getMinimumOrderQuantity() );


        try {
            itemRepository.save(itemToBeInserted);
            var refreshedItem = itemRepository.findById(itemToBeInserted.getId());
            if (refreshedItem == null) {
                message = "Item " + itemToBeInserted.getDescription() + " cant be re-retrieved after seemingly successful insert.";
                itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
                logger.error(message);
            }
            itemCrudBatchResponse.getData().add( refreshedItem );
        } catch (Exception exception) {
            var error = translateExceptionToError(exception, itemCrudToBeCrud);
            itemCrudBatchResponse.addError(error);
        }
    }
private void changeItem(Item updatedItem,
                        ItemCrudBatchResponse itemCrudBatchResponse) {

        var priorItem = itemRepository.findById(updatedItem.getId());

        if (priorItem == null) {
            outputErrorAndThrow( String.format( ITEM_REF_NOT_FOUND, "ChangeItem", updatedItem.getId() ), itemCrudBatchResponse, logger );
        }
        if ( updatedItem.getId() != priorItem.getId() ) {
            outputErrorAndThrow( String.format(  ITEM_IDS_MUST_BE_SAME, updatedItem.getId(), priorItem.getId() ), itemCrudBatchResponse, logger );
        }
        try {
        itemRepository.save(updatedItem);
        itemCrudBatchResponse.getData().add( updatedItem);
        } catch(Exception exception){
            var error = translateExceptionToError(exception, updatedItem);
            itemCrudBatchResponse.addError(error);
        }
    }


    private void deleteItem(Item itemCrudToBeCrud,
                            ItemCrudBatchResponse itemCrudBatchResponse) {
        try {
            Item toBeDeletedItem = itemRepository.findById(itemCrudToBeCrud.getId());
            if (toBeDeletedItem == null) {

                 if ( itemCrudToBeCrud.getCrudAction() == CrudAction.DELETE) {
                    var message = "Unable to find item " + itemCrudToBeCrud.getId() + " in database.";
                    itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
                    logger.info(message);
                 } else {
                     var message = "Silent delete on " + itemCrudToBeCrud.getId() + " in database.";
                     itemCrudBatchResponse.getData().add( itemCrudToBeCrud );
                     logger.info(message);
                 }
            } else {
                itemRepository.deleteById(toBeDeletedItem.getId());
                itemCrudBatchResponse.getData().add( toBeDeletedItem );
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

    ErrorLine translateExceptionToError(Exception exception, Item itemCrudSingle) {
        String message;

        if (exception instanceof DataIntegrityViolationException) {
            message = itemCrudSingle.getCrudAction() + " failed on " + itemCrudSingle.getId() + ":" +
                    itemCrudSingle.getDescription() + " due to " +
                    generateErrorMessageFrom((DataIntegrityViolationException) exception);
            logger.error(message);
            return new ErrorLine(1, message);
        }
        message = itemCrudSingle.getCrudAction() + " failed on " + itemCrudSingle.getId() + ":" +
                itemCrudSingle.getDescription() + " due to " +
                exception.getMessage();
        logger.error(message);
        return new ErrorLine(1, message);
    }

    @CrossOrigin
    @RequestMapping(value = ItemCrudRequestURL, method = RequestMethod.POST)
    public ResponseEntity<?> itemCrudRequest(@RequestBody ItemCrudBatch itemCrudBatch) {

        ItemCrudBatchResponse itemCrudBatchResponse = new ItemCrudBatchResponse();
        try {
            itemCrudBatchResponse = go(itemCrudBatch, itemCrudBatchResponse );
            itemCrudBatchResponse.setResponseType(ResponseType.MULTILINE );
            if (itemCrudBatchResponse.getData().isEmpty()) {
                var message = "No items were processed, either due to errors or no actionable inputs.";
                logger.info(message);
                itemCrudBatchResponse.getErrors().add(new ErrorLine(1, message));
            }
        } catch (Exception exception) {
            logger.info( exception.getMessage());
        }
        return ResponseEntity.ok().body(itemCrudBatchResponse);
    }



    public ItemCrudBatchResponse query( ItemCrudBatch itemCrudBatch, ItemCrudBatchResponse itemCrudBatchResponse ) {
        ArrayList<Item> queryResults;
        if ( itemCrudBatch.updatedRows().length == 0 ) {
            queryResults = (ArrayList<Item>) itemRepository.findAll();
        } else {
            Specification<Item> dynamicQuery = ItemSpecifications.withDynamicQuery( itemCrudBatch.updatedRows()[0] );
            queryResults = itemRepository.findAll( dynamicQuery );
        }

        logger.info("Query had {} rows", queryResults.size() );
        itemCrudBatchResponse.setData( queryResults);
        return itemCrudBatchResponse;
    }

    @CrossOrigin
    @RequestMapping(value = ItemCrudQuery, method = RequestMethod.POST)
    public ResponseEntity<?> itemCrudQuery(@RequestBody ItemCrudBatch itemQueryParameters ) {

        ItemCrudBatchResponse queryResults = new ItemCrudBatchResponse();
        try {
            queryResults = query( itemQueryParameters, queryResults );
            queryResults.setResponseType(ResponseType.MULTILINE );
            if (queryResults.getData().isEmpty()) {
                var message = "No items were processed, either due to errors or no actionable inputs.";
                logger.info(message);
                queryResults.getErrors().add(new ErrorLine(1, message));
            }
        } catch (Exception exception) {
            logger.info( exception.getMessage());
        }
        return ResponseEntity.ok().body(queryResults);
    }


    public ItemCrud( @Autowired ItemRepository itemRepository ) {
        this.itemRepository = itemRepository;
    }
}

