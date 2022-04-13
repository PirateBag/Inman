package com.inman.business;

import com.inman.entity.Item;
import com.inman.model.rest.ErrorLine;
import com.inman.model.rest.ItemAddRequest;
import com.inman.model.response.ResponsePackage;
import com.inman.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemAddLogic {
	
	@Transactional
	public Item[] go(ItemRepository itemRepository, ItemAddRequest addItemRequest ) {
		Item item = null;
		Item newItem = null;

		item = new Item(addItemRequest);
		newItem = itemRepository.saveAndFlush(item);
		Item [] items = new Item[ 1 ];
		items[ 0 ] = newItem;
		return items;
	}

	public ResponsePackage persistItem(ItemRepository xItemRepository, ItemAddRequest xAddItemRequest )
	{
		ResponsePackage responsePackage = new ResponsePackage();
		Item [] items = null;

		try {
			items = go( xItemRepository, xAddItemRequest );
			responsePackage.setData( items );
		} catch ( Exception e ) {
			if ( e.getMessage().contains( "PUBLIC.ITEM(SUMMARY_ID)")) {
				responsePackage.addError( new ErrorLine( 0, "0", "Duplicate Summary Id, provide a unqiue value.") );

			}
		}
		return responsePackage;
	}
}
