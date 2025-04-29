package com.inman.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

import java.util.ArrayList;


@Configuration
@RestController
public class ItemCrud {
    public static final String UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION = "Unique index or primary key violation";
    public static final String ItemCrudRequestURL = "item/crud";

    @Autowired
    ItemRepository itemRepository;

    static Logger logger = LoggerFactory.getLogger("controller: " + ItemCrud.class);

    public ItemCrudBatchResponse go(ItemRepository itemRepository, ItemCrudBatch itemCrudBatch) {
        String message = "";
        ArrayList<ItemCrudSingle> itemCrudSinglesForResponse = new ArrayList<>();
        ArrayList<ErrorLine> errors = new ArrayList<>();
        int lineNumber = 0 ;

        for (ItemCrudSingle itemCrudToBeCrud : itemCrudBatch.updatedRows()) {
/*
			if (updatedBom.getActivityState() == ActivityState.CHANGE) {
				oldBom = bomRepository.findById(updatedBom.getId());
				if (oldBom.isEmpty()) {
					message = "Unable to retrieve the original Bom instance for id " + updatedBom.getId();
					bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
					logger.error(message);
					throw new RuntimeException(message);
				}

				if (updatedBom.getQuantityPer() == oldBom.get().getQuantityPer()) {
					message = "Bom " + updatedBom.getId() + " quantityPer field did not change.";
					logger.warn(message);
					bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
				} else {
					logger.info("Bom " + updatedBom.getId() + " quantityPer was updated from " + oldBom.get().getQuantityPer() + " to " + updatedBom.getQuantityPer());
					oldBom.get().setQuantityPer(updatedBom.getQuantityPer());
					bomResponse = updateMaxDepthOf( updatedBom, bomResponse );
					bomRepository.save(oldBom.get());
					var refreshedBom = bomPresentRepository.findById(updatedBom.getId());
					refreshedBom.setActivityState(ActivityState.CHANGE);

					updatedBomsToReturn.add(refreshedBom);
				}
			}   */
            if (itemCrudToBeCrud.getActivityState() == ActivityState.INSERT) {
                logger.info(itemCrudToBeCrud.getActivityState() + " on " + itemCrudToBeCrud);
                Item itemToBeInserted = new Item(itemCrudToBeCrud.getSummaryId(), itemCrudToBeCrud.getDescription(), itemCrudToBeCrud.getUnitCost(), itemCrudToBeCrud.getSourcing());
                try {
                    itemRepository.save(itemToBeInserted);
                    var refreshedItem = itemRepository.findBySummaryId(itemToBeInserted.getSummaryId());
                    if (refreshedItem == null) {
                        message = "Item " + itemToBeInserted.getSummaryId() + " unable to re-retrieve inserted ";
                        errors.add( new ErrorLine( 1, "str", message ));
                        logger.error(message);
                    }

                    itemCrudSinglesForResponse.add( new ItemCrudSingle(refreshedItem.getSummaryId(), refreshedItem.getDescription(),
                            refreshedItem.getUnitCost(), refreshedItem.getSourcing(), ActivityState.INSERT) );

                } catch (DataIntegrityViolationException dataIntegrityViolationException) {
                    message = "Unable to insert " + itemCrudToBeCrud.getSummaryId() + ":" +
                            itemCrudToBeCrud.getDescription() + " due to " +
                            generateErrorMessageFrom(dataIntegrityViolationException);
                    errors.add( new ErrorLine( 1, "str", message ));
                    logger.error(message);
                }
                catch (Exception e) {
                    message = "Unable to delete " + itemCrudToBeCrud.getSummaryId() + ":" +
                            itemCrudToBeCrud.getDescription() + " due to " +
                            e.getMessage();
                    errors.add( new ErrorLine( 1, "str", message ));
                    logger.error(message);
                }
            } else if (itemCrudToBeCrud.getActivityState() == ActivityState.DELETE) {
                logger.info(itemCrudToBeCrud.getActivityState() + " on " + itemCrudToBeCrud.toString());

                try {
                    Item toBeDeletedItem = itemRepository.findBySummaryId(itemCrudToBeCrud.getSummaryId());
                    if (toBeDeletedItem == null) {
                        logger.info("Unable to find and will be ignored " + itemCrudToBeCrud.toString());
                    } else {
                        itemRepository.deleteById(toBeDeletedItem.getId());
                        itemCrudSinglesForResponse.add(
                        new ItemCrudSingle(toBeDeletedItem.getSummaryId(), toBeDeletedItem.getDescription(),
                                toBeDeletedItem.getUnitCost(), toBeDeletedItem.getSourcing(), ActivityState.DELETE));
                    }
                } catch (DataIntegrityViolationException dataIntegrityViolationException) {
                    message = "Unable to delete " + itemCrudToBeCrud.getSummaryId() + ":" +
                            itemCrudToBeCrud.getDescription() + " due to " +
                            generateErrorMessageFrom(dataIntegrityViolationException);
                    errors.add( new ErrorLine( 1, "ABC", message ));
                    logger.error(message);
                } catch (Exception e) {
                    message = "Unable to delete " + itemCrudToBeCrud.getSummaryId() + ":" +
                            itemCrudToBeCrud.getDescription() + " due to " +
                            e.getMessage();
                    errors.add( new ErrorLine( lineNumber, "str", message ));
                    logger.error(message);
                }
            } else {
                logger.info("Item " + itemCrudToBeCrud.getSummaryId() + " was ignored because ActivtyState was " + itemCrudToBeCrud.getActivityState());
            }
            lineNumber++;
        }
        ItemCrudBatchResponse itemCrudBatchResponse =  new ItemCrudBatchResponse( itemCrudSinglesForResponse, errors );
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT );
        try {
            System.out.println(objectMapper.writeValueAsString(itemCrudBatchResponse));
            System.out.println("-----");
            System.out.println(objectMapper.writeValueAsString(itemCrudBatchResponse.getData() ));
        } catch ( Exception e) {
            System.out.println(e.getMessage());
        }
        return new ItemCrudBatchResponse( itemCrudSinglesForResponse, errors );
    }


    private String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
        var detailedMessage = dataIntegrityViolationException.getMessage();
        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
        }
        return detailedMessage;
    }


    @CrossOrigin
    @RequestMapping(value = ItemCrudRequestURL, method = RequestMethod.POST)
    public ResponseEntity<?> itemCrudRequest(@RequestBody ItemCrudBatch itemCrudBatch) {
        ItemCrudBatchResponse itemCrudBatchResponse = go(itemRepository, itemCrudBatch);
        itemCrudBatchResponse.setResponseType(ResponseType.ADD);
        if (true /* responsePackage.getData().length == 0 */  ) {
            var message = "No items were processed, either due to errors or no actionable inputs.";
            logger.info(message);
            itemCrudBatchResponse.getErrors().add(new ErrorLine(1, "abc", message));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT );
        try {
            System.out.println(objectMapper.writeValueAsString(itemCrudBatchResponse));
        } catch ( Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok().body(itemCrudBatchResponse);
    }
}

