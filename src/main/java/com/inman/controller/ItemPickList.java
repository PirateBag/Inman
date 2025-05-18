package com.inman.controller;

import com.inman.business.ItemPickListLogic;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.ItemPickListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

		return ResponseEntity.ok().body( responsePackage );
	}

	@RequestMapping( value = GenericSingleId.ITEMS_FOR_BOM_URL, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> itemsForBom( @RequestBody GenericSingleId genericSingleId)
	{
		ItemPickListResponse responsePackage = itemPickListLogic.getItemsForBom(genericSingleId) ;

		return ResponseEntity.ok().body( responsePackage );
	}

	@RequestMapping( value = GenericSingleId.GET_ONE_ITEM, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> getOneItem( @RequestBody GenericSingleId genericSingleId)
	{
		ItemPickListResponse responsePackage = itemPickListLogic.getOneItem(genericSingleId) ;

		return ResponseEntity.ok().body( responsePackage );
	}
}

