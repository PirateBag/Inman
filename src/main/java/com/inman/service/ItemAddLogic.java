package com.inman.service;

import com.inman.entity.Item;
import com.inman.model.response.ItemResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.model.rest.ItemAddRequest;
import com.inman.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemAddLogic {
	
	@Transactional
	public Item[] go(ItemRepository itemRepository, ItemAddRequest addItemRequest ) {
		Item newItem = null;
		throw new UnsupportedOperationException();
		/*var item = new Item(addItemRequest);
		newItem = itemRepository.saveAndFlush(item);
		Item [] items = new Item[ ] { newItem };
		reurn items;  */
	}

	public ItemResponse persistItem(ItemRepository xItemRepository, ItemAddRequest xAddItemRequest )
	{
		ItemResponse responsePackage = new ItemResponse( ResponseType.ADD );
		try {
			var items = go( xItemRepository, xAddItemRequest );
			responsePackage.getData().add( items[ 0 ]);
		} catch ( Exception e ) {
			if ( e.getMessage().contains( "PUBLIC.ITEM(SUMMARY_ID)")) {
				responsePackage.addError( new ErrorLine( 0, "0", "Duplicate Summary Id, provide a unqiue value.") );
			}
		}
		return responsePackage;
	}
}
