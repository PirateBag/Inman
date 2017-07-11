package com.inman.business;

import org.springframework.transaction.annotation.Transactional;

import com.inman.model.Item;
import com.inman.model.rest.ItemAddRequest;
import com.inman.model.rest.ItemDeleteRequest;
import com.inman.repository.ItemRepository;

public class ItemDeleteLogic {
	
	@Transactional
	public Item[] go(ItemRepository itemRepository, ItemDeleteRequest itemDeleteRequest ) {

		itemRepository.deleteById( itemDeleteRequest.getId() );
		Item newItem = itemRepository.saveAndFlush( item );
		Item [] items = new Item[ 0 ];
		items[ 0 ] = item;
		return items;
	}
}
