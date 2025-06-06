package com.inman.controller;

import com.inman.business.BomCrudService;
import com.inman.business.BomSearchLogic;
import com.inman.business.BomLogicService;
import com.inman.entity.BomPresent;
import com.inman.entity.Text;
import com.inman.model.request.*;
import com.inman.model.response.BomResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.response.TextResponse;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.BomRepository;
import com.inman.repository.ItemRepository;
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

	@Autowired
	BomPresentRepository bomPresentRepository;

	@Autowired
	BomSearchLogic bomSearchLogic;

	@Autowired
	BomRepository bomRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	BomLogicService bomLogicService;

	BomCrudService bomCrudService;

	@Autowired
	public Bom(BomCrudService bomCrudService,
			   BomLogicService bomLogicService) {
		this.bomCrudService = bomCrudService;
		this.bomLogicService = bomLogicService;
	}

	static Logger logger = LoggerFactory.getLogger("controller: " + Bom.class);

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

	@CrossOrigin
	@RequestMapping(value = BomSearchRequest.findByParent, method = RequestMethod.POST)
	public ResponseEntity<?> bomFindByParent(@RequestBody BomSearchRequest xBomSearchRequest) {

		BomPresent[] boms = bomPresentRepository.findByParentId(xBomSearchRequest.getIdToSearchFor().intValue());
		BomResponse responsePackage = new BomResponse();
		responsePackage.setData((ArrayList<BomPresent>) Arrays.asList(boms));
		responsePackage.setResponseType(ResponseType.QUERY);
		return ResponseEntity.ok().body(responsePackage);
	}


	@CrossOrigin
	@RequestMapping(value = BomCrudBatch.updateUrl, method = RequestMethod.POST)
	public ResponseEntity<BomResponse> bomUpdateArray(@RequestBody BomPresent[] xComponents) {
		BomResponse responsePackage = bomCrudService.applyBomUpdates(bomRepository, bomPresentRepository, xComponents);
		return ResponseEntity.ok().body(responsePackage);

	}

	@CrossOrigin
	@RequestMapping(value = BomCrudBatch.bomCrud, method = RequestMethod.POST)
	public ResponseEntity<BomResponse> bomUpdateArray(@RequestBody BomCrudBatch bomCrudBatch) {
		BomResponse responsePackage = new BomResponse();
		try {

			responsePackage = bomCrudService.applyBomUpdates(bomRepository, bomPresentRepository, bomCrudBatch.getUpdatedRows());
		} catch ( RuntimeException runtimeException ) {
			logger.error( "Encountered RuntimeException in service, look for rollback.");
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

		bomLogicService.updateMaxDepthOf( itemToRefresh.getIdToSearchFor(), texts );
		texts.add( new Text( "Report Comppleted" ) );
		rValue.setData( texts );
		rValue.setResponseType(ResponseType.QUERY);
		return rValue;
	}
}