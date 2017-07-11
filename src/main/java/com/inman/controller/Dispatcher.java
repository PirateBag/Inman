package com.inman.controller;

import org.springframework.web.bind.annotation.RestController;

import com.inman.business.VerifyCredentialsLogic;
import com.inman.business.ItemAddLogic;
import com.inman.business.ItemDeleteLogic;
import com.inman.business.ItemSearchLogic;
import com.inman.business.Message;
import com.inman.business.QueryParameterException;
import com.inman.model.rest.ErrorLine;
import com.inman.model.rest.PrepareResponse;
import com.inman.model.rest.ResponsePackage;
import com.inman.model.rest.ItemResponse;
import com.inman.model.rest.SearchItemRequest;
import com.inman.model.Item;
import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;
import com.inman.model.rest.ItemAddRequest;
import com.inman.model.rest.ItemDeleteRequest;
import com.inman.prepare.ItemPrepare;
import com.inman.repository.ItemRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Configuration
@RestController
public class Dispatcher {
	
	@Autowired
	private ItemRepository itemRepository;


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
    @RequestMapping( "/show" )
    public @ResponseBody List<Item> show( ) {
    	
    	ItemPrepare itemPrepare = new ItemPrepare();
    	return itemPrepare.show( itemRepository );
    }
    
    @CrossOrigin
    @RequestMapping( value = SearchItemRequest.singleUrl, method=RequestMethod.GET )
    public @ResponseBody Item[] searchItem(@PathVariable long itemId ) {
    	
    	if ( !Application.isPrepared() ) {
        	ItemPrepare itemPrepare = new ItemPrepare();
        	itemPrepare.go( itemRepository );
        	Application.setIsPrepared( true );
    	}
    	
    	ItemSearchLogic itemSearch = new ItemSearchLogic();
    	return itemSearch.findById( itemRepository, itemId );
    }
    

    @CrossOrigin
    @RequestMapping( value = SearchItemRequest.queryUrl, method=RequestMethod.GET )
    public ResponseEntity<?> searchItemExpGeneric(
    		@RequestParam( "id") String id,
    		@RequestParam( "summaryId") String summaryId,
    		@RequestParam( "description") String description ) {
    	
    	if ( !Application.isPrepared() ) {
        	ItemPrepare itemPrepare = new ItemPrepare();
        	itemPrepare.go( itemRepository );
        	Application.setIsPrepared( true );
    	}
    	
    	ItemResponse responsePackage = new ItemResponse();
    	
    	SearchItemRequest searchItemRequest = null;
    	try {
    		searchItemRequest = new SearchItemRequest( id, summaryId, description );
    		ItemSearchLogic itemSearch = new ItemSearchLogic();
    		Item[] items = itemSearch.bySearchItemRequest( itemRepository, searchItemRequest );
    		if ( items.length == 0 ) {
    			responsePackage.addError( new ErrorLine( 0, "0", Message.NO_DATA_FOR_PARAMETERS ));
    		}
    		responsePackage.setData( items );
    	} catch ( QueryParameterException e ) {
    		responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
    		responsePackage.setData( new Item[0] );
    	}
    	
    	return ResponseEntity.ok().body(responsePackage);
    }
    
    @CrossOrigin
    @RequestMapping( value = ItemAddRequest.addUrl, method=RequestMethod.GET )
    public ResponseEntity<?> itemAdd(
    		@RequestParam( "summaryId") String summaryId,
    		@RequestParam( "description") String description,
    		@RequestParam( "unitCost") double unitCost ) {
    	
    	if ( !Application.isPrepared() ) {
        	ItemPrepare itemPrepare = new ItemPrepare();
        	itemPrepare.go( itemRepository );
        	Application.setIsPrepared( true );
    	}
    	
    	
    	ItemResponse responsePackage = new ItemResponse();
    	ItemAddRequest addItemRequest = null;
    	try {
    		addItemRequest = new ItemAddRequest( summaryId, description, unitCost );
    		ItemAddLogic addItemLogic = new ItemAddLogic();
    		Item [] items = addItemLogic.go( itemRepository, addItemRequest );
    		if ( items.length == 0 ) {
    			responsePackage.addError( new ErrorLine( 0, "0", Message.NO_DATA_FOR_PARAMETERS ));
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
    @RequestMapping( value = ItemDeleteRequest.deleteUrl, method=RequestMethod.GET )
    public ResponseEntity<?> itemDelete(
    		@RequestParam( "id") String id ) {
    	
    	if ( !Application.isPrepared() ) {
        	ItemPrepare itemPrepare = new ItemPrepare();
        	itemPrepare.go( itemRepository );
        	Application.setIsPrepared( true );
    	}
    	
    	
    	ItemResponse responsePackage = new ItemResponse();
    	ItemDeleteRequest itemDeleteRequest = null;
    	try {
    		itemDeleteRequest = new ItemDeleteRequest( id );
    		ItemDeleteLogic itemDeleteLogic = new ItemDeleteLogic();
    		Item [] items = itemDeleteLogic.go( itemRepository, itemDeleteRequest );
    		if ( items.length == 0 ) {
    			responsePackage.addError( new ErrorLine( 0, "0", Message.NO_DATA_FOR_PARAMETERS ));
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
}

