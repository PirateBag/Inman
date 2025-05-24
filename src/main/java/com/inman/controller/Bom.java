package com.inman.controller;

import com.inman.business.BomCrudService;
import com.inman.business.BomSearchLogic;
import com.inman.business.BomNavigation;
import com.inman.entity.ActivityState;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.model.request.BomPresentSearchRequest;
import com.inman.model.request.BomSearchRequest;
import com.inman.model.request.BomCrudBatch;
import com.inman.model.response.BomResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.BomRepository;
import com.inman.repository.DdlRepository;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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

	BomCrudService bomCrudService;

	@Autowired
	public Bom( BomCrudService bomCrudService) {
		this.bomCrudService = bomCrudService;
	}

	static Logger logger = LoggerFactory.getLogger("controller: " + Bom.class);


	/**
	 * ,
	 *   { "parentId" :  "1", "childId" :  "3", "quantityPer" :  "1.00", "activityState" :  "INSERT" },
	 *   { "parentId" :  "1", "childId" :  "4", "quantityPer" :  "1.00", "activityState" :  "INSERT" },
	 *   { "parentId" :  "1", "childId" :  "5", "quantityPer" :  "5.00", "activityState" :  "INSERT" }
	 * @param dataIntegrityViolationException
	 * @return
	 */


	private BomResponse updateMaxDepthOf(BomPresent updatedBom, BomResponse bomResponse) {
		Item component = itemRepository.findById(updatedBom.getChildId());
		Item parent = itemRepository.findById(updatedBom.getParentId());

		if (component.getMaxDepth() <= parent.getMaxDepth()) {
			int newMaxDepth = parent.getMaxDepth() + 1;
			logger.info(component.getId() + " depth changing from " + component.getMaxDepth() + " to " + parent.getMaxDepth() + 1);
			component.setMaxDepth(newMaxDepth);
			itemRepository.save(component);
			return bomResponse;
		}
		logger.info(component.getId() + " depth not changing " + component.getMaxDepth() + " to " + parent.getMaxDepth() + 1);
		return bomResponse;
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

}