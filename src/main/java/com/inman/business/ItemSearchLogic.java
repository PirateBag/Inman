package com.inman.business;

import com.inman.model.Item;
import com.inman.model.rest.ResponsePackage;
import com.inman.model.rest.SearchItemRequest;
import com.inman.repository.ItemRepository;

/*
public class ItemSearchLogic {
	public Item[] findById( ItemRepository itemRepository, long itemId ) {
		Item[] items= new Item[1];
		items[ 0 ] = itemRepository.findById( itemId );
		if ( items[ 0 ] == null ) {
			items = new Item[ 0 ];
		}
		return items;
		
	}*/

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
		
		if ( searchItemRequest.getItemId() != 0 ) {
			return findById( itemRepository, searchItemRequest.getItemId() );
		}

		if ( searchItemRequest.getSummaryId() != null ) {
			return itemRepository.byleadingSummaryId( searchItemRequest.getSummaryId() );
		}

		return null;
	}
}
