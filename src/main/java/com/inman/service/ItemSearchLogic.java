package com.inman.service;

import com.inman.entity.Item;
import com.inman.model.rest.SearchItemRequest;
import com.inman.repository.ItemRepository;

import java.util.List;


public class ItemSearchLogic {
	public Item[] findById( ItemRepository itemRepository, long itemId ) {
		
		
		Item[] items= new Item[1];
		items[ 0 ] = itemRepository.findById( itemId );
		if ( items[ 0 ] == null ) {
			items = new Item[ 0 ];
		}
		return items;
		
	}
	public Item[] bySearchItemRequest(ItemRepository itemRepository, SearchItemRequest searchItemRequest) {
		
		if ( searchItemRequest.getId() != 0 ) {
			return findById( itemRepository, searchItemRequest.getId() );
		}

		if ( searchItemRequest.getDescription() != null ) {
			return itemRepository.byDescription( searchItemRequest.getDescription() );
		}
		
		List<Item> items = itemRepository.findAll();
		
		Item[] returnValue = items.toArray( new Item[ items.size()]); 
		return returnValue;
	}
}
