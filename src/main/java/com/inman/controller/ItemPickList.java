package com.inman.controller;

import com.inman.service.ItemPickListLogic;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.ItemPickListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.inman.controller.LoggingUtility.outputInfoToLog;


@Configuration
@RestController
public class ItemPickList {
	@Autowired
	ItemPickListLogic itemPickListLogic;

	@CrossOrigin
	@RequestMapping( value = GenericSingleId.all, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> allItemPickList( )
	{
		ItemPickListResponse responsePackage = itemPickListLogic.getAll( ) ;
		outputInfoToLog( "Return all items with a length of " + responsePackage.getData().size() );
		return ResponseEntity.ok().body( responsePackage );
	}

	@RequestMapping( value = GenericSingleId.ITEMS_FOR_BOM_URL, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> itemsForBom( @RequestBody GenericSingleId genericSingleId)
	{
		ItemPickListResponse responsePackage = itemPickListLogic.getItemsForBom(genericSingleId) ;
		outputInfoToLog( "Return BOM items with a length of " + responsePackage.getData().size() );
		return ResponseEntity.ok().body( responsePackage );
	}

	@RequestMapping( value = GenericSingleId.GET_ONE_ITEM, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> getOneItem( @RequestBody GenericSingleId genericSingleId)
	{

		ItemPickListResponse responsePackage = itemPickListLogic.getOneItem(genericSingleId) ;
		outputInfoToLog( "Return one with a length of " + responsePackage.getData().size() );
		return ResponseEntity.ok().body( responsePackage );
	}
}

