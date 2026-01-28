package com.inman.service;
import enums.CrudAction;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.model.request.BomPresentSearchRequest;
import com.inman.model.response.BomResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.BomRepository;
import com.inman.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;

import static com.inman.controller.LoggingUtility.outputInfoToLog;
import static com.inman.controller.LoggingUtility.outputInfoToResponse;
import static com.inman.controller.Messages.*;
import static com.inman.controller.Utility.generateErrorMessageFrom;

@Service
public class BomCrudService {

    static Logger logger = LoggerFactory.getLogger( BomCrudService.class );

    BomPresentRepository bomPresentRepository;
    ItemRepository itemRepository;
    BomRepository bomRepository;
    BomLogicService bomLogicService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public BomCrudService(BomPresentRepository bomPresentRepository,
                          ItemRepository itemRepository,
                          BomRepository bomRepository,
                          BomLogicService bomLogicService) {
        this.bomPresentRepository = bomPresentRepository;
        this.itemRepository = itemRepository;
        this.bomRepository = bomRepository;
        this.bomLogicService = bomLogicService;
    }



    private void updateMaxDepthOf(BomPresent updatedBom ) {
        Item component = itemRepository.findById(updatedBom.getChildId());
        Item parent = itemRepository.findById(updatedBom.getParentId());

        if (component.getMaxDepth() <= parent.getMaxDepth()) {
            int newMaxDepth = parent.getMaxDepth() + 1;
            logger.info("Depth change {} component is at or lower {} than parent {}.", component.getId(), component.getMaxDepth(), parent.getMaxDepth());
            component.setMaxDepth(newMaxDepth);
            itemRepository.save(component);
            return;
        }
        logger.info("No change in depth {} component is deeper {} than parent {}.", component.getId(), component.getMaxDepth(), parent.getMaxDepth());
    }
    @Transactional
    /**
     * Depending on the type of crudAction, process each element of the array to be updated.
     *  BomResponse is an I/O parameter that will be filled in with error and return values.  A return value was not used due to the
     * need to throw exceptions to cause rollbacks.
     * xBomPresentToUpdate is an array of updates to be executed.
     * Returns void as the outcome is the side effect changes to bomResponse.
     */
    public void applyBomUpdates( BomResponse bomResponse, BomPresent[] xBomPresentToUpdate ) {
        bomResponse.setResponseType(ResponseType.CHANGE);
        for (BomPresent updatedBom : xBomPresentToUpdate) {
            logger.info("Bom {},{} {}, {}", updatedBom.getParentId(), updatedBom.getCrudAction(), updatedBom.getChildId(), updatedBom.getQuantityPer());

            switch (updatedBom.getCrudAction()) {
                case CHANGE -> change(updatedBom, bomResponse);
                case INSERT -> insert(updatedBom, bomResponse);
                default -> {
                    var message = String.format(ILLEGAL_STATE_MESSAGE.text(), "BOM " + updatedBom.getCrudAction() + updatedBom.getId());
                    logger.error(message);
                    bomResponse.addError(new ErrorLine(ILLEGAL_STATE_MESSAGE.httpStatus(), message));
                    throw new RuntimeException(message);
                }
            }
        }

        logger.info("Update loop exited with " + bomResponse.getErrors().size() + " errors");
        RuntimeException runtimeException = null;
        for (ErrorLine errorLine : bomResponse.getErrors()) {
            logger.info("Error: " + errorLine.toString() );
            if (errorLine.getStatus() != HttpStatus.OK ) {
                runtimeException = new RuntimeException( errorLine.getMessage() );
            }
        }
        if (runtimeException != null) {
            throw  runtimeException;
        }
    }


    @CrossOrigin
    @RequestMapping(value = BomPresentSearchRequest.all, method = RequestMethod.POST)
    public ResponseEntity<?> bomPresentFindAll(@RequestBody BomPresentSearchRequest xBomPresentSearchRequest) {
        BomPresent[] boms;
        if (xBomPresentSearchRequest.getIdToSearchFor() == 0) {
            boms = bomPresentRepository.findAll().toArray(new BomPresent[0]);
        } else {
            BomPresent bom = bomPresentRepository.findById(xBomPresentSearchRequest.getIdToSearchFor());
            boms = new BomPresent[1];
            boms[0] = bom;
        }

        BomResponse responsePackage = new BomResponse();
        responsePackage.setData((ArrayList<BomPresent>) Arrays.asList(boms));

        return ResponseEntity.ok().body(responsePackage);
    }


    private void change(BomPresent updatedBom, BomResponse bomResponse ) {
        var oldBom = bomRepository.findById(updatedBom.getId());
        String message;
        if (oldBom.isEmpty()) {
            message = String.format( ORIGINAL_NOT_FOUND.text(), "BOM", updatedBom.getId() );
            bomResponse.addError(new ErrorLine( ORIGINAL_NOT_FOUND.httpStatus(), message));
            logger.error(message);
            throw new RuntimeException(message);
        }

        if (updatedBom.getQuantityPer() == oldBom.get().getQuantityPer()) {
            message = String.format( QUANTITY_PER_DID_NOT_CHANGE.text(), "BOM",  updatedBom.getId() );
            bomResponse.addError(new ErrorLine( QUANTITY_PER_DID_NOT_CHANGE.httpStatus(), message));
            logger.warn(message);
        } else {
            final String updateMessage = QUANTITY_PER_UPDATED.text().formatted( updatedBom.getId(), oldBom.get().getQuantityPer(), updatedBom.getQuantityPer() );
            oldBom.get().setQuantityPer(updatedBom.getQuantityPer());

            bomRepository.saveAndFlush(oldBom.get());
            var refreshedBom = bomPresentRepository.findById(updatedBom.getId());
            entityManager.refresh(refreshedBom);
            outputInfoToLog(  "Refreshed BOM is " + refreshedBom.toString());
            outputInfoToLog(  "old refreshed BOM is " + oldBom.get().toString());
            refreshedBom.setCrudAction(CrudAction.CHANGE);
            bomResponse.getData().add(refreshedBom);
            outputInfoToResponse( HttpStatus.OK, updateMessage, bomResponse );
        }

        updateMaxDepthOf(updatedBom );
    }

    private void insert(BomPresent updatedBom, BomResponse bomResponse ) {

        com.inman.entity.Bom bomToBeInserted = new com.inman.entity.Bom(updatedBom.getParentId(), updatedBom.getChildId(), updatedBom.getQuantityPer());
        try {
            bomLogicService.isItemIdInWhereUsed(updatedBom.getParentId(),
                    bomToBeInserted.getChildId());
            com.inman.entity.Bom insertedBom = bomRepository.saveAndFlush(bomToBeInserted);
            var refreshedBom = bomPresentRepository.byParentIdChildId(bomToBeInserted.getParentId(), bomToBeInserted.getChildId());
            if (refreshedBom == null) {
                var message = RERETRIEVE.text().formatted( "Bom", insertedBom.getId() );
                logger.error(message);
                bomResponse.addError(new ErrorLine(RERETRIEVE.httpStatus(), message));
                return;
            }
            entityManager.refresh(refreshedBom);
            refreshedBom.setCrudAction(CrudAction.INSERT);
            bomResponse.getData().add(refreshedBom);
            updateMaxDepthOf(updatedBom );

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
           var message = DATA_INTEGRITY.text().formatted(  bomToBeInserted.getParentId() +
                    bomToBeInserted.getChildId(), generateErrorMessageFrom(dataIntegrityViolationException) );
            logger.error(message);
            bomResponse.addError(new ErrorLine(DATA_INTEGRITY.httpStatus(), message));
            throw  dataIntegrityViolationException;
        }
    }
}
