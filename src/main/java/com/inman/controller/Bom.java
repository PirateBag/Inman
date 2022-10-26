package com.inman.controller;

import com.inman.business.BomSearchLogic;
import com.inman.entity.BomPresent;
import com.inman.model.request.BomPresentSearchRequest;
import com.inman.model.request.BomSearchRequest;
import com.inman.model.response.BomResponse;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.repository.BomPresentRepository;
import com.inman.model.request.BomUpdateRequest;
import com.inman.repository.BomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Configuration
@RestController
public class Bom {
	@Autowired
	BomPresentRepository bomPresentRepository;

	@Autowired
	BomSearchLogic bomSearchLogic;

	@Autowired
	BomRepository bomRepository;

	static Logger logger = LoggerFactory.getLogger( "conteroller: " + Bom.class );

	public BomResponse go( BomRepository xBomRepository, BomPresentRepository bomPresentRepository, BomUpdateRequest xUpdateBomRequest ) {

		Optional<com.inman.entity.Bom> bom = bomRepository.findById( xUpdateBomRequest.getId() );

		if ( bom.isEmpty() ) {
			String message = "Unable to retrieve the raw Bom instance for id " + xUpdateBomRequest.getId();
			logger.error ( message );
			throw new RuntimeException( message );
		}

		bom.get().setQuantityPer( xUpdateBomRequest.getQuantityPer() );
		bomRepository.save( bom.get() );

		BomPresent[] bomPresents = new BomPresent[1];
		bomPresents[0] = bomPresentRepository.findById( ( bom.get().getId() ));

		BomResponse bomResponse = new BomResponse( ResponseType.CHANGE, bomPresents );

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

		BomPresent[] boms = bomPresentRepository.byParentId(  xBomSearchRequest.getIdToSearchFor() );
		ResponsePackage responsePackage = new ResponsePackage( boms, ResponseType.QUERY );
		return ResponseEntity.ok().body( responsePackage );
	}


	@CrossOrigin
	@RequestMapping( value = BomUpdateRequest.updateUrl, method=RequestMethod.POST )
	public ResponseEntity<?> bomUpdateId( @RequestBody BomUpdateRequest xBomUpdateRequest  )
	{
		com.inman.entity.Bom[] boms = bomSearchLogic.byId( bomRepository, xBomUpdateRequest.getId()  );

		ResponsePackage responsePackage =  go( bomRepository, bomPresentRepository, xBomUpdateRequest );
				new ResponsePackage( boms, ResponseType.QUERY );

		return ResponseEntity.ok().body( responsePackage );
	}
}

