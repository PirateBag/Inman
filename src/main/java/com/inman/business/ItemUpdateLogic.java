package com.inman.business;

import org.springframework.transaction.annotation.Transactional;

import com.inman.model.Item;
import com.inman.model.rest.ItemUpdateRequest;
import com.inman.repository.ItemRepository;

public class ItemUpdateLogic {
	
	@Transactional
	public Item[] go(ItemRepository itemRepository, ItemUpdateRequest updateItemRequest ) {

		Item item = itemRepository.findById( updateItemRequest.getId() );
		item.setSummaryId( updateItemRequest.getSummaryId() );
		item.setDescription( updateItemRequest.getDescription() );
		item.setUnitCost( updateItemRequest.getUnitCost() );
		itemRepository.save( item );
		
		System.out.println( "Updated item " + updateItemRequest.getId() );

		Item [] items = new Item[ 1 ];
		items[ 0 ] = item;
		return items;
	}
}
