package com.inman.controller;

import com.inman.entity.BomPresent;
import com.inman.entity.Text;
import com.inman.model.request.*;
import com.inman.model.response.BomResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.response.TextResponse;
import com.inman.repository.BomPresentRepository;
import com.inman.service.BomCrudService;
import com.inman.service.BomLogicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;


@Configuration
@RestController
public class Bom {
	public static final String UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION = "Unique index or primary key violation";

    BomPresentRepository bomPresentRepository;

	BomLogicService bomLogicService;

	BomCrudService bomCrudService;

	@Autowired
	public Bom(BomCrudService bomCrudService,

			   BomLogicService bomLogicService,
			   BomPresentRepository bomPresentRepository
	) {
		this.bomCrudService = bomCrudService;
		this.bomLogicService = bomLogicService;
		this.bomPresentRepository = bomPresentRepository;
	}

	static Logger logger = LoggerFactory.getLogger( Bom.class);

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
		responsePackage.setData(new ArrayList<>(Arrays.asList(boms)));

		return ResponseEntity.ok().body(responsePackage);
	}

	@CrossOrigin
	@RequestMapping(value = BomSearchRequest.findByParent, method = RequestMethod.POST)
	public ResponseEntity<?> bomFindByParent(@RequestBody BomSearchRequest xBomSearchRequest) {
		return commonFindUsingItemParameters( xBomSearchRequest.getIdToSearchFor() );
	}

	@CrossOrigin
	@RequestMapping(value = BomSearchRequest.findUsingItemParameters, method = RequestMethod.POST)
	public ResponseEntity<?> bomFindUsingItemParametersPost(@RequestBody ItemCrudBatch itemCrudBatch ) {
		return commonFindUsingItemParameters( itemCrudBatch.updatedRows()[ 0 ].getId() );
	}

	private ResponseEntity<?> commonFindUsingItemParameters( Long idToSearchFor ) {
		BomResponse responsePackage = new BomResponse();
		BomPresent[] boms = bomPresentRepository.findByParentId( idToSearchFor );
		responsePackage.setData(new ArrayList<>(Arrays.asList(boms)));
		responsePackage.setResponseType(ResponseType.QUERY);
		return ResponseEntity.ok().body(responsePackage);
	}

	@CrossOrigin
	@RequestMapping(value = BomCrudBatch.bomCrud, method = RequestMethod.POST)
	public ResponseEntity<BomResponse> bomUpdateArray(@RequestBody BomCrudBatch bomCrudBatch) {
		BomResponse responsePackage = new BomResponse();
    	try {
			bomCrudService.applyBomUpdates( responsePackage, bomCrudBatch.getUpdatedRows());
		} catch ( RuntimeException runtimeException ) {
			logger.error( "Encountered RuntimeException " + runtimeException + "look for rollback.");
            return ResponseEntity.badRequest().body(responsePackage);
		}
		return ResponseEntity.ok().body(responsePackage);
	}

	@CrossOrigin
	@RequestMapping(value = ItemReportRequest.WHERE_USED_REPORT_URL, method = RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	private TextResponse updateMaxDepth(@RequestBody GenericSingleId itemToRefresh ) {
		var rValue = new TextResponse();

		ArrayList<Text> texts = new ArrayList<>();

		bomLogicService.updateMaxDepthOf( itemToRefresh.idToSearchFor(), texts );
		texts.add( new Text( "Report Completed" ) );
		rValue.setData( texts );
		rValue.setResponseType(ResponseType.QUERY);
		return rValue;
	}
}