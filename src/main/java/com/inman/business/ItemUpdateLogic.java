package com.inman.business;

import com.inman.entity.Item;
import com.inman.model.rest.ItemResponse;
import com.inman.model.rest.ItemUpdateRequest;
import com.inman.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		
		Item [] items = new Item[ 1 ];
		items[ 0 ] = item;
		
		itemResponse.setData( items );
		return itemResponse;
	}
}
