package com.inman.business;

import com.inman.model.Item;
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

}
