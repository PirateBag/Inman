package com.inman.business;

import com.inman.entity.Item;
import com.inman.model.response.ItemResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.model.rest.ItemUpdateRequest;
import com.inman.model.response.ResponsePackage;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemUpdateLogic {
	static Logger logger = LoggerFactory.getLogger(ItemUpdateLogic.class + "go" );
	@Transactional
	public ResponsePackage go(ItemRepository itemRepository, ItemUpdateRequest updateItemRequest ) {

		ItemResponse itemResponse = new ItemResponse(ResponseType.CHANGE );

		Item item = itemRepository.findById( updateItemRequest.getId() );
		
		if ( item == null ) {
			var errorMessage = "Item Id " + updateItemRequest.getId() + " not found";
			itemResponse.addError( new ErrorLine( 1, "100", errorMessage ));
			logger.info( errorMessage );
			return itemResponse;
		}

		logger.info( "Successfully retrieved " + item.getId() + " for update" );
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
