package com.inman.controller;

import com.inman.business.ItemPickListLogic;
import com.inman.model.request.ItemPickListRequest;
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
	@RequestMapping( value = ItemPickListRequest.all, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> allItemPickList( )
	{
		ItemPickListResponse responsePackage = itemPickListLogic.getAll( ) ;

		return ResponseEntity.ok().body( responsePackage );
	}

	@RequestMapping( value = ItemPickListRequest.ITEMS_FOR_BOM_URL, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> itemsForBom( @RequestBody ItemPickListRequest itemPickListRequest  )
	{
		ItemPickListResponse responsePackage = itemPickListLogic.getItemsForBom( itemPickListRequest ) ;

		return ResponseEntity.ok().body( responsePackage );
	}

	@RequestMapping( value = ItemPickListRequest.GET_ONE_ITEM, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<ItemPickListResponse> getOneItem( @RequestBody ItemPickListRequest itemPickListRequest  )
	{
		ItemPickListResponse responsePackage = itemPickListLogic.getOneItem( itemPickListRequest ) ;

		return ResponseEntity.ok().body( responsePackage );
	}
}

