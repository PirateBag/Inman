package com.inman.business;

import com.inman.entity.ActivityState;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.model.request.BomPresentSearchRequest;
import com.inman.model.response.BomResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.BomRepository;
import com.inman.repository.ItemRepository;
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

import static com.inman.controller.Bom.UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
import static com.inman.controller.Utility.generateErrorMessageFrom;

@Service
public class BomCrudService {

    static Logger logger = LoggerFactory.getLogger("controller: " + com.inman.controller.Bom.class);

    BomPresentRepository bomPresentRepository;
    ItemRepository itemRepository;
    BomRepository bomRepository;
    BomLogicService bomLogicService;

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


    @Transactional
    public BomResponse applyBomUpdates(BomRepository xBomRepository, BomPresentRepository bomPresentRepository, BomPresent[] xBomPresentToUpdate) {
        var bomResponse = new BomResponse();
        bomResponse.setResponseType(ResponseType.CHANGE);
        int lineNumber = 0;

        for (BomPresent updatedBom : xBomPresentToUpdate) {
            logger.info("Bom {},{} {}, {}", updatedBom.getParentId(), updatedBom.getActivityState(), updatedBom.getChildId(), updatedBom.getQuantityPer());

            switch ( updatedBom.getActivityState() ) {
                case CHANGE ->  change(updatedBom, bomResponse, lineNumber);
                case INSERT ->  insert(updatedBom, bomResponse, lineNumber);
                default ->  {
                    logger.info( "Activity state " + updatedBom.getActivityState() + " not supported" );
                    throw new RuntimeException( "Bom " + updatedBom.getParentId() + "," + updatedBom.getActivityState() );
                }
            }
            lineNumber++;
        }

        logger.info("Update loop exited with " + bomResponse.getErrors().size() + " errors");
        if (!bomResponse.getErrors().isEmpty()) {
            logger.info("Ideally throwing exception");
            throw new RuntimeException("at least one error occurred");
        }
        return bomResponse;
    }




    private void updateMaxDepthOf(BomPresent updatedBom ) {
        Item component = itemRepository.findById(updatedBom.getChildId());
        Item parent = itemRepository.findById(updatedBom.getParentId());

        if (component.getMaxDepth() <= parent.getMaxDepth()) {
            int newMaxDepth = parent.getMaxDepth() + 1;
            logger.info("{} depth changing from {} to {}" + 1, component.getId(), component.getMaxDepth(), parent.getMaxDepth());
            component.setMaxDepth(newMaxDepth);
            itemRepository.save(component);
            return;
        }
        logger.info("{} depth not changing {} to {}" + 1, component.getId(), component.getMaxDepth(), parent.getMaxDepth());
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


	/*
	@CrossOrigin
	@RequestMapping( value = BomUpdate.INVALID_COMPONENTS_URL, method=RequestMethod.POST )
	public ResponseEntity<?> bomFindInvalidComponents( @RequestBody BomPresent[] proposedComponents  )
	{
		ResponsePackage responsePackage = bomNavigation.isItemIdInWhereUsed( proposedComponents );

		return ResponseEntity.ok().body( responsePackage );
	}
	*/


    private void change(BomPresent updatedBom, BomResponse bomResponse, int lineNumber) {
        var oldBom = bomRepository.findById(updatedBom.getId());
        String message = "";
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

            bomRepository.save(oldBom.get());
            var refreshedBom = bomPresentRepository.findById(updatedBom.getId());
            refreshedBom.setActivityState(ActivityState.CHANGE);
            bomResponse.getData().add(refreshedBom);
        }

        updateMaxDepthOf(updatedBom );
    }

    private void insert(BomPresent updatedBom, BomResponse bomResponse, int lineNumber) {
        String message;
        com.inman.entity.Bom bomToBeInserted = new com.inman.entity.Bom(updatedBom.getParentId(), updatedBom.getChildId(), updatedBom.getQuantityPer());
        com.inman.entity.Bom insertedBom = null;
        try {
            bomLogicService.isItemIdInWhereUsed(updatedBom.getParentId(),
                    bomToBeInserted.getChildId());
            insertedBom = bomRepository.save(bomToBeInserted);
            logger.info( "insertedBom" + insertedBom );
            var refreshedBom = bomPresentRepository.byParentIdChildId(insertedBom.getParentId(), bomToBeInserted.getChildId());
            if (refreshedBom == null) {
                message = "Bom " + insertedBom.getId() + " unable to re-retrieve inserted BOM ";
                logger.error(message);
                bomResponse.addError(new ErrorLine(lineNumber, message));
                return;
            }

            refreshedBom.setActivityState(ActivityState.INSERT);
            bomResponse.getData().add(refreshedBom);
            updateMaxDepthOf(updatedBom );

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            message = "Unable to insert " + bomToBeInserted.getParentId() + ":" +
                    bomToBeInserted.getChildId() + " due to " +
                    generateErrorMessageFrom(dataIntegrityViolationException);
            logger.error(message);
            bomResponse.addError(new ErrorLine(lineNumber, message));
        }
    }
}
