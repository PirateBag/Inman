package com.inman.controller;

import com.inman.business.BomPresentSearchLogic;
import com.inman.entity.BomPresent;
import com.inman.model.request.BomPresentSearchRequest;
import com.inman.model.request.BomSearchRequest;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.repository.BomPresentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Configuration
@RestController
public class Bom {

	@Autowired
	BomPresentRepository bomPresentRepository;

	@Autowired
	BomPresentSearchLogic bomPresentSearchLogic;

	@CrossOrigin
	@RequestMapping( value = BomPresentSearchRequest.all, method=RequestMethod.POST )
	public ResponseEntity<?> bomPresentFindAll( )
	{
		BomPresent[] boms = bomPresentRepository.findAll().toArray(new BomPresent[0]);

		//  BomPresent[] bomPresents = bomPresentSearchLogic.byAll( bomPresentRepository  );
		ResponsePackage responsePackage = new ResponsePackage( boms, ResponseType.QUERY );

		return ResponseEntity.ok().body( responsePackage );
	}

	@CrossOrigin
	@RequestMapping( value = BomSearchRequest.findByParent, method=RequestMethod.POST )
	public ResponseEntity<?> bomFindByParent( @RequestBody BomSearchRequest xBomSearchRequest	) {

		BomPresent[] boms = bomPresentRepository.byParentId(  xBomSearchRequest.getIdToSearchFor() );
		ResponsePackage responsePackage = new ResponsePackage( boms, ResponseType.QUERY );
		return ResponseEntity.ok().body( responsePackage );
	}


/*
	@CrossOrigin
	@RequestMapping( value = BomSearchRequest.findById, method=RequestMethod.POST )
	public ResponseEntity<?> bomFindById( @RequestBody BomSearchRequest xBomSearchRequest )
	//  public ResponseEntity<?> bomFindById( @RequestBody String xBomSearchRequestString )
	{

		makeSureBasicContentIsReady();
		Bom[] boms = bomSearchLogic.byId( bomRepository, xBomSearchRequest.getIdToSearchFor()  );

		ResponsePackage responsePackage = new ResponsePackage( boms, ResponseType.QUERY );

		return ResponseEntity.ok().body( responsePackage );
	}


 */


}

