package com.inman.controller;

import com.inman.repository.*;
import com.inman.service.*;
import com.inman.entity.Item;
import com.inman.entity.Text;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.ItemResponse;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Configuration
@RestController
public class ItemAndStatus {
	static Logger logger = LoggerFactory.getLogger( ItemAndStatus.class);
	static Logger dividerLogger = LoggerFactory.getLogger(" ");
	public static final String CLEAR_ALL_DATA = "clearAllData";

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BomRepository bomRepository;
	
	@Autowired
	private ItemUpdateLogic itemUpdateLogic;
	
	@Autowired
	private ItemAddLogic itemAddLogic;

	@Autowired
	private DdlRepository ddlRepository;

	@Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private AdjustmentRepository adjustmentRepository;

	@Autowired
	ItemAndStatus( OrderLineItemRepository orderLineItemRepository) {
		this.orderLineItemRepository = orderLineItemRepository;
	}


	@CrossOrigin
    @RequestMapping( StatusResponse.rootUrl )
    public StatusResponse status() {
    	StatusResponse statusResponse = new StatusResponse();
    	statusResponse.setStatus( StatusResponse.INMAN_OK );
    	return statusResponse;
    }
    
    @CrossOrigin
    @RequestMapping( value = SearchItemRequest.singleUrl, method=RequestMethod.GET )
    public @ResponseBody Item[] searchItem(@PathVariable long itemId ) {

		ItemSearchLogic itemSearch = new ItemSearchLogic();
    	return itemSearch.findById( itemRepository, itemId );
    }

	@CrossOrigin
    @RequestMapping( value = SearchItemRequest.queryUrl, method=RequestMethod.POST,
    		consumes = "application/json",
    		produces = "application/json")
    public ResponseEntity<?> searchItemExpGeneric( @RequestBody SearchItemRequest request ) {

		ResponsePackage<Item> responsePackage = new ItemResponse( ResponseType.QUERY );
    	
    	try {
    		ItemSearchLogic itemSearch = new ItemSearchLogic();
    		Item[] items = itemSearch.bySearchItemRequest( itemRepository, request );
    		if ( items.length == 0 ) {
    			responsePackage.addError( new ErrorLine( 0, "0", Messages.NO_DATA_FOR_PARAMETERS ));
    		}
    		responsePackage.getData().add( items[ 0 ] );
    	} catch ( Exception e ) {
    		responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
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
				responsePackage.addError( new ErrorLine( 0, "0", Messages.NO_DATA_FOR_PARAMETERS ));
			}

			responsePackage.getData().add( items[ 0 ] );
		} catch ( Exception e ) {
			responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
		}

		return ResponseEntity.ok().body(responsePackage);
	}
    
    @CrossOrigin
    @RequestMapping( value = ItemAddRequest.addUrl, method=RequestMethod.POST )
    public ResponseEntity<?> itemAdd(
    		@RequestBody ItemAddRequest itemAddRequest )
    {
		var responsePackage = itemAddLogic.persistItem( itemRepository, itemAddRequest );

    	return ResponseEntity.ok().body( responsePackage );
    }
    
    @CrossOrigin
    @RequestMapping( value = ItemDeleteRequest.deleteUrl, method=RequestMethod.GET )
    public ResponseEntity<?> itemDelete(
    		@RequestParam( "id") String id ) {

		ItemResponse responsePackage = new ItemResponse( ResponseType.DELETE );
    	ItemDeleteRequest itemDeleteRequest = null;
    	try {
    		itemDeleteRequest = new ItemDeleteRequest( id );
    		ItemDeleteLogic itemDeleteLogic = new ItemDeleteLogic();
    		Item [] items = itemDeleteLogic.go( itemRepository, itemDeleteRequest );
    		if ( items.length != 0 ) {
    			responsePackage.addError( new ErrorLine( 0, "0", 
    					String.format( Messages.WRONG_NUMBER_OF_PARAMETERS, 0, items.length )) );
    		}
    		responsePackage.getData().add( items[ 0 ] );
    		
    	} catch ( QueryParameterException e ) {
    		responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));

    	} catch ( Exception e ) {
    		responsePackage.addError( new ErrorLine( 0, "0", e.getMessage() ));
    	}
    	return ResponseEntity.ok().body( responsePackage );
    }

	@CrossOrigin
	@RequestMapping( value = StatusResponse.toLog, method=RequestMethod.GET )
	public StatusResponse toLog( @RequestParam String testName  )
	{
		Application.setTestName( testName );
		dividerLogger.info( testName );
		StatusResponse statusResponse = new StatusResponse();
		statusResponse.setStatus( testName );
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

	@CrossOrigin
	@RequestMapping( value = ItemAndStatus.CLEAR_ALL_DATA, method=RequestMethod.POST )
	public ResponsePackage<Text> clearAllData(@RequestBody GenericSingleId genericSingleId )
	{
		ResponsePackage<Text> responsePackage;

		if ( genericSingleId.idToSearchFor() == 1L ) {
			responsePackage = clearAllData(itemRepository);
		} else 	if ( genericSingleId.idToSearchFor() == 2L ) {
			responsePackage = clearAllOrders(itemRepository);
		} else {
			responsePackage = new ResponsePackage<>();
			responsePackage.addError( new ErrorLine( 1, "Id must be 1 for all, 2 for orders only.") );
		}
		return responsePackage;
	}

	private ResponsePackage<Text> clearAllOrders(ItemRepository itemRepository) {
		var rValue = new ResponsePackage<Text>();
		orderLineItemRepository.deleteAllInBatch();
		rValue.getData().add( new Text( "OrdereLineItems deleted" ) );

		ddlRepository.resetIdForTable( "order_line_item" );
		rValue.getData().add( new Text( "Order Line Items PK reset" ) );

		return rValue;
	}


	public ResponsePackage<Text> clearAllData( ItemRepository itemRepository ){
		itemRepository.deleteAllInBatch();
		var rValue = new ResponsePackage<Text>();
		rValue.getData().add( new Text( "Items deleted" ) );

		bomRepository.deleteAllInBatch();
		rValue.getData().add( new Text( "BOMs deleted" ) );

		orderLineItemRepository.deleteAllInBatch();
		rValue.getData().add( new Text( "OrdereLineItems deleted" ) );

		adjustmentRepository.deleteAllInBatch();
		rValue.getData().add( new Text( "Adjustments deleted" ) );

		ddlRepository.resetIdForTable( "Item" );
		rValue.getData().add( new Text( "Items PK Reset" ) );

		ddlRepository.resetIdForTable( "Bom" );
		rValue.getData().add( new Text( "BOMs PK Reset" ) );

		ddlRepository.resetIdForTable( "order_line_item" );
		rValue.getData().add( new Text( "Order Line Items PK reset" ) );

		ddlRepository.resetIdForTable( "adjustment" );
		rValue.getData().add( new Text( "Adjustment PK reset" ) );

		return rValue;
	}
}

