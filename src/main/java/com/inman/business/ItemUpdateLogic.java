package com.inman.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inman.model.Item;
import com.inman.model.rest.ItemResponse;
import com.inman.model.rest.ItemUpdateRequest;
import com.inman.repository.ItemRepository;

@Service
public class ItemUpdateLogic {
	
	@Transactional
	public ItemResponse go(ItemRepository itemRepository, ItemUpdateRequest updateItemRequest ) {
		
		ItemResponse itemResponse = new ItemResponse();

		Item item = itemRepository.findById( updateItemRequest.getId() );
		
		if ( item == null ) {
			
		}
		item.setSummaryId( updateItemRequest.getSummaryId() );
		item.setDescription( updateItemRequest.getDescription() );
		item.setUnitCost( updateItemRequest.getUnitCost() );
		itemRepository.save( item );
		
		System.out.println( "Updated item " + updateItemRequest.getId() );

		Item [] items = new Item[ 1 ];
		items[ 0 ] = item;
		
		itemResponse.setData( items );
		return itemResponse;
	}
}
