package com.inman.business;

import org.springframework.transaction.annotation.Transactional;

import com.inman.model.Item;
import com.inman.model.rest.ItemAddRequest;
import com.inman.repository.ItemRepository;

public class ItemAddLogic {
	
	@Transactional
	public Item[] go(ItemRepository itemRepository, ItemAddRequest addItemRequest ) {

		Item item = new Item( addItemRequest );
		Item newItem = itemRepository.saveAndFlush( item );
		Item [] items = new Item[ 1 ];
		items[ 0 ] = item;
		return items;
	}
}
