package com.inman.business;

import org.springframework.transaction.annotation.Transactional;

import com.inman.model.Item;
import com.inman.model.rest.AddItemRequest;
import com.inman.model.rest.PrepareResponse;
import com.inman.repository.ItemRepository;

public class AddItemLogic {
	
	@Transactional
	public PrepareResponse go(ItemRepository itemRepository, AddItemRequest addItemRequest ) {

		Item item = new Item( addItemRequest );
		itemRepository.save( item );
		return new PrepareResponse( AddItemRequest.addUrl, 1 );
		
	}
}
