package com.inman.controller;

import com.inman.business.BomSearchLogic;
import com.inman.business.BomNavigation;
import com.inman.entity.ActivityState;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.model.request.BomPresentSearchRequest;
import com.inman.model.request.BomSearchRequest;
import com.inman.model.request.BomUpdate;
import com.inman.model.request.ItemReportRequest;
import com.inman.model.response.BomResponse;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.BomRepository;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;


@Configuration
@RestController
public class Bom {
	public static final String UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION = "Unique index or primary key violation";
	@Autowired
	BomPresentRepository bomPresentRepository;

	@Autowired
	BomSearchLogic bomSearchLogic;

	@Autowired
	BomRepository bomRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	BomNavigation bomNavigation;

	static Logger logger = LoggerFactory.getLogger( "controller: " + Bom.class );

	public BomResponse go( BomRepository xBomRepository, BomPresentRepository bomPresentRepository, BomPresent[] xBomPresentToUpdate  ) {
		var bomResponse = new BomResponse();
		bomResponse.setResponseType( ResponseType.CHANGE );
		String message = "";
		int lineNumber = 0;
		ArrayList<BomPresent> updatedBomsToReturn = new ArrayList<>();

		for ( BomPresent updatedBom : xBomPresentToUpdate ) {
			Optional<com.inman.entity.Bom> oldBom;

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
			} else if (updatedBom.getActivityState() == ActivityState.INSERT) {
				logger.info("Bom Insert  " + updatedBom.getParentId() + "," + updatedBom.getChildId() + ", " + updatedBom.getQuantityPer());
				com.inman.entity.Bom bomToBeInserted = new com.inman.entity.Bom(updatedBom.getParentId(), updatedBom.getChildId(), updatedBom.getQuantityPer());
				com.inman.entity.Bom insertedBom = null;
				try {
					bomNavigation.isItemIdInWhereUsed( updatedBom.getParentId(),
							bomToBeInserted.getChildId() );
					 insertedBom = bomRepository.save(bomToBeInserted);
					var refreshedBom = bomPresentRepository.byParentIdChildId(insertedBom.getParentId(), bomToBeInserted.getChildId());
					if (refreshedBom == null) {
						message = "Bom " + insertedBom.getId() + " unable to re-retrieve inserted BOM ";
						logger.error(message);
						bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
					}
					refreshedBom.setActivityState(ActivityState.INSERT);
					updatedBomsToReturn.add(refreshedBom);

				} catch ( DataIntegrityViolationException dataIntegrityViolationException ) {
					message = "Unable to insert " + bomToBeInserted.getParentId() + ":" +
							bomToBeInserted.getChildId() + " due to " +
							generateErrorMessageFrom(  dataIntegrityViolationException );
					logger.error( message );
					bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
				}


			} else {
				logger.info( "Bom " + updatedBom.getId() + " was ignored because ActivtyState was " + updatedBom.getActivityState() );
			}
			lineNumber++;
		}

		bomResponse.setData( updatedBomsToReturn.toArray( new BomPresent[ updatedBomsToReturn.size() ] ) );
		return bomResponse;
	}


	private String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
		var detailedMessage = dataIntegrityViolationException.getMessage();
		if ( detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION) ) {
			return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
		}
		return detailedMessage;
	}

	private BomResponse updateMaxDepthOf( BomPresent updatedBom, BomResponse bomResponse) {
		Item component = itemRepository.findById( updatedBom.getChildId() );
		Item parent = itemRepository.findById( updatedBom.getParentId()  );

		if ( component.getMaxDepth() <= parent.getMaxDepth() ) {
			int newMaxDepth = parent.getMaxDepth() + 1;
			logger.info( component.getId() + " depth changing from " + component.getMaxDepth() + " to " + parent.getMaxDepth() + 1 );
			component.setMaxDepth( newMaxDepth );
			itemRepository.save( component );
			return bomResponse;
		}
		logger.info( component.getId() + " depth not changing " + component.getMaxDepth() + " to " + parent.getMaxDepth() + 1 );
		return bomResponse;
	}


	@CrossOrigin
	@RequestMapping( value = BomPresentSearchRequest.all, method=RequestMethod.POST )
	public ResponseEntity<?> bomPresentFindAll(@RequestBody BomPresentSearchRequest xBomPresentSearchRequest  ) {
		BomPresent[] boms;
		if (xBomPresentSearchRequest.getIdToSearchFor() == 0) {
			boms = bomPresentRepository.findAll().toArray(new BomPresent[0]);
		} else {
			BomPresent bom = bomPresentRepository.findById(xBomPresentSearchRequest.getIdToSearchFor());
			boms = new BomPresent[1];
			boms[ 0 ] = bom;
		}

		ResponsePackage responsePackage = new ResponsePackage(boms, ResponseType.QUERY);

		return ResponseEntity.ok().body(responsePackage);
	}

	@CrossOrigin
	@RequestMapping( value = BomSearchRequest.findByParent, method=RequestMethod.POST )
	public ResponseEntity<?> bomFindByParent( @RequestBody BomSearchRequest xBomSearchRequest	) {

		BomPresent[] boms = bomPresentRepository.findByParentId(  xBomSearchRequest.getIdToSearchFor().intValue() );
		ResponsePackage responsePackage = new ResponsePackage( boms, ResponseType.QUERY );
		return ResponseEntity.ok().body( responsePackage );
	}



	@CrossOrigin
	@RequestMapping( value = BomUpdate.updateUrl, method=RequestMethod.POST )
	public ResponseEntity<?> bomUpdateArray( @RequestBody BomPresent[] xComponents  )
	{
		ResponsePackage responsePackage = go( bomRepository, bomPresentRepository, xComponents );

		return ResponseEntity.ok().body( responsePackage );
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

}

