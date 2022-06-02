package com.inman.controller;

import com.inman.business.*;
import com.inman.entity.Item;
import com.inman.model.MetaData;
import com.inman.model.response.ItemResponse;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.*;
import com.inman.prepare.ItemPrepare;
import com.inman.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Configuration
@RestController
public class ItemAndStatus {
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private ItemUpdateLogic itemUpdateLogic;
	
	@Autowired
	private ItemAddLogic itemAddLogic;

	@CrossOrigin
    @RequestMapping( StatusResponse.rootUrl )
    public StatusResponse status() {
    	StatusResponse statusResponse = new StatusResponse();
    	statusResponse.setStatus( StatusResponse.INMAN_OK );
    	return statusResponse;
    }
    
    @CrossOrigin
    @RequestMapping( value = VerifyCredentialsRequest.rootUrl,method = RequestMethod.POST,
    		consumes = "application/json",
    		produces = "application/json" )
    public VerifyCredentialsResponse verifyCredentials( @RequestBody VerifyCredentialsRequest request) {
    	
    	VerifyCredentialsLogic verifyCredentialsLogic = new VerifyCredentialsLogic();
    	
    			
    	return verifyCredentialsLogic.handle( request );
    }
    
    @CrossOrigin
    @RequestMapping( PrepareResponse.rootUrl )
    public PrepareResponse prepare( ) {
    	
    	ItemPrepare itemPrepare = new ItemPrepare();
    	return itemPrepare.go( itemRepository );
    }
    
    @CrossOrigin
    @RequestMapping( value = SearchItemRequest.singleUrl, method=RequestMethod.GET )
    public @ResponseBody Item[] searchItem(@PathVariable long itemId ) {

		makeSureBasicContentIsReady();

		ItemSearchLogic itemSearch = new ItemSearchLogic();
    	return itemSearch.findById( itemRepository, itemId );
    }

	@CrossOrigin
    @RequestMapping( value = SearchItemRequest.queryUrl, method=RequestMethod.POST,
    		consumes = "application/json",
    		produces = "application/json")
    public ResponseEntity<?> searchItemExpGeneric( @RequestBody SearchItemRequest request ) {

		makeSureBasicContentIsReady();

		ResponsePackage responsePackage = new ItemResponse( ResponseType.QUERY );
    	
    	try {
    		ItemSearchLogic itemSearch = new ItemSearchLogic();
    		Item[] items = itemSearch.bySearchItemRequest( itemRepository, request );
    		if ( items.length == 0 ) {
    			responsePackage.addError( new ErrorLine( 0, "0", Message.NO_DATA_FOR_PARAMETERS ));
    		}

    		responsePackage.setData( items );
    	} catch ( Exception e ) {
    		responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
    		responsePackage.setData( new Item[0] );
    	}
    	
    	return ResponseEntity.ok().body(responsePackage);
    }

	@CrossOrigin
	@RequestMapping( value = SearchItemRequest.allUrl, method=RequestMethod.POST,
			produces = "application/json")
	public ResponseEntity<?> searchItemFindAll( ) {

		//		makeSureBasicContentIsReady();

		ResponsePackage responsePackage = new ItemResponse( ResponseType.QUERY );

		try {
			Item[] items = itemRepository.findAll().toArray(new Item[0]);
			if ( items.length == 0 ) {
				responsePackage.addError( new ErrorLine( 0, "0", Message.NO_DATA_FOR_PARAMETERS ));
			}

			responsePackage.setData( items );
		} catch ( Exception e ) {
			responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
			responsePackage.setData( new Item[0] );
		}

		return ResponseEntity.ok().body(responsePackage);
	}
    
    @CrossOrigin
    @RequestMapping( value = ItemAddRequest.addUrl, method=RequestMethod.POST )
    public ResponseEntity<?> itemAdd(
    		@RequestBody ItemAddRequest itemAddRequest )
    {
		makeSureBasicContentIsReady();


		var responsePackage = itemAddLogic.persistItem( itemRepository, itemAddRequest );

    	return ResponseEntity.ok().body( responsePackage );
    }
    
    @CrossOrigin
    @RequestMapping( value = ItemDeleteRequest.deleteUrl, method=RequestMethod.GET )
    public ResponseEntity<?> itemDelete(
    		@RequestParam( "id") String id ) {

		makeSureBasicContentIsReady();

		ItemResponse responsePackage = new ItemResponse( ResponseType.DELETE );
    	ItemDeleteRequest itemDeleteRequest = null;
    	try {
    		itemDeleteRequest = new ItemDeleteRequest( id );
    		ItemDeleteLogic itemDeleteLogic = new ItemDeleteLogic();
    		Item [] items = itemDeleteLogic.go( itemRepository, itemDeleteRequest );
    		if ( items.length != 0 ) {
    			responsePackage.addError( new ErrorLine( 0, "0", 
    					String.format( Message.WRONG_NUMBER_OF_PARAMETERS, 0, items.length )) );
    		}
    		responsePackage.setData( items );
    		
    	} catch ( QueryParameterException e ) {
    		responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
    		responsePackage.setData( new Item[0] );
   		
    	} catch ( Exception e ) {
    		responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
    		responsePackage.setData( new Item[0] );
    	}
    	return ResponseEntity.ok().body( responsePackage );
    }
    
    @CrossOrigin
    @RequestMapping( value = StatusResponse.metaDataUrl,
    				method=RequestMethod.GET )
    public StatusResponse metaData() throws ClassNotFoundException {
    	StatusResponse statusResponse = new StatusResponse();
    	statusResponse.setStatus( MetaData.show( Item.class.getCanonicalName() ) );
    	return statusResponse;
    }
    
    @CrossOrigin
    @RequestMapping( value = ItemUpdateRequest.updateUrl, method=RequestMethod.POST )
    public ResponsePackage itemUpdate(
			@RequestBody ItemUpdateRequest itemUpdateRequest )
    {
		ResponsePackage responsePackage = itemUpdateLogic.go( itemRepository, itemUpdateRequest );
 
    	return responsePackage;
    }
	private void makeSureBasicContentIsReady() {
		if ( !Application.isPrepared() ) {
			/*
			new ItemPrepare().go( itemRepository );
			new BomPrepare().go( bomRepository ); */
			Application.setIsPrepared( true );
		}
	}
}

