package com.inman.controller;

import com.inman.entity.Item;
import com.inman.entity.Pick;
import com.inman.model.request.ItemPickListRequest;
import com.inman.model.response.ItemPickListResponse;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Configuration
@RestController
public class ItemPickList {
	@Autowired
	ItemRepository itemRepository;
	static Logger logger = LoggerFactory.getLogger( "controller: " + ItemPickList.class );

	public ItemPickListResponse go( ItemPickListRequest itemPickListRequest, ItemRepository itemRepository ) {
		var itemPickListResponse = new ItemPickListResponse();
		itemPickListResponse.setResponseType( ResponseType.ADD );
		String message = "";

		Item[] items = itemRepository.findAll().toArray(new Item[0]);
		Pick[] picks = new Pick[ items.length ];
		int i = 0;
		for ( Item item : items ) {
			picks[ i++ ] = new Pick( item.getId(), Pick.formatExternalFromSummaryDescription( item.getSummaryId(), item.getDescription() ) );
		}

		itemPickListResponse.setData(picks);
		return itemPickListResponse;
	}




	@CrossOrigin
	@RequestMapping( value = ItemPickListRequest.all, method=RequestMethod.POST,
			consumes = "application/json",
			produces = "application/json")
	public ResponseEntity<?> allItemPickList( @RequestBody ItemPickListRequest itemPickListRequest  )
	{
		ResponsePackage responsePackage = go( itemPickListRequest, itemRepository ) ;

		return ResponseEntity.ok().body( responsePackage );
	}
}

