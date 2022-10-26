package com.inman.business;

import com.inman.entity.Item;
import com.inman.model.response.ItemResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ItemUpdateRequest;
import com.inman.model.response.ResponsePackage;
import com.inman.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemUpdateLogic {
	
	@Transactional
	public ResponsePackage go(ItemRepository itemRepository, ItemUpdateRequest updateItemRequest ) {

		ItemResponse itemResponse = new ItemResponse(ResponseType.CHANGE );

		Item item = itemRepository.findById( updateItemRequest.getId() );
		
		if ( item == null ) {
			
		}
		item.setSummaryId( updateItemRequest.getSummaryId() );
		item.setDescription( updateItemRequest.getDescription() );
		item.setUnitCost( updateItemRequest.getUnitCost() );
		item.setSourcing( updateItemRequest.getSourcing() );
		itemRepository.save( item );
		
		Item [] items = new Item[ ] { item };

		itemResponse.setData( items );
		return itemResponse;
	}
}
