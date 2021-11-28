package com.inman.business;

import org.springframework.transaction.annotation.Transactional;

import com.inman.model.Item;
import com.inman.model.rest.ItemDeleteRequest;
import com.inman.repository.ItemRepository;

public class ItemDeleteLogic {
	
	@Transactional
	public Item[] go(ItemRepository itemRepository, ItemDeleteRequest itemDeleteRequest ) {

		//  Item item = itemRepository.findById( itemDeleteRequest.getId() );
		//  itemRepository.delete( itemDeleteRequest.getId() );
		itemRepository.flush();
		
		Item [] items = new Item[ 0 ];
		
		System.out.println( "Deleted item " + itemDeleteRequest.getId() );
		return items;
	}
}
