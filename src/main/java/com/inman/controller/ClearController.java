package com.inman.controller;

import com.inman.entity.Text;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.ResponsePackage;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;


@Configuration
@RestController
public class ClearController {
	static Logger dividerLogger = LoggerFactory.getLogger(" ");
	public static final String CLEAR_ALL_DATA = "clearAllData";
	public static final String CLEAR_ITEM_BALANCES = "clear/itemBalances";

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BomRepository bomRepository;

	@Autowired
	private DdlRepository ddlRepository;

	@Autowired
    private OrderLineItemRepository orderLineItemRepository;

    @Autowired
    private AdjustmentRepository adjustmentRepository;

	@Autowired
    ClearController(OrderLineItemRepository orderLineItemRepository) {
		this.orderLineItemRepository = orderLineItemRepository;
	}



	@CrossOrigin
	@RequestMapping( value = ClearController.CLEAR_ALL_DATA, method=RequestMethod.POST )
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

	@CrossOrigin
	@RequestMapping( value = ClearController.CLEAR_ITEM_BALANCES, method=RequestMethod.POST )
	public ResponsePackage<Text> clearItemBalancesController(@RequestBody GenericSingleId genericSingleId )
	{
		ResponsePackage<Text> responsePackage;
		responsePackage = clearItemBalancesService(itemRepository);
		return responsePackage;
	}


	private ResponsePackage<Text> clearAllOrders(ItemRepository itemRepository) {
		var rValue = new ResponsePackage<Text>();
		orderLineItemRepository.deleteAllInBatch();
		rValue.getData().add( new Text( "All OrdereLineItems deleted." ) );

		ddlRepository.resetIdForTable( "order_line_item" );
		rValue.getData().add( new Text( "Order Line Items PK reset" ) );
		return rValue;
	}

	private ResponsePackage<Text> clearItemBalancesService(ItemRepository itemRepository) {
		var rValue = new ResponsePackage<Text>();
		int count = ddlRepository.resetItemBalances();
		rValue.getData().add( new Text( "%d Items cleared".formatted( count ) ) );
		return rValue;
	}

	public ResponsePackage<Text> clearAllData( ItemRepository itemRepository ){

		var rValue = new ResponsePackage<Text>();

		itemRepository.deleteAllInBatch();
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

