package com.inman.prepare;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.inman.model.Item;
import com.inman.model.rest.PrepareResponse;
import com.inman.repository.ItemRepository;

public class ItemPrepare {
	

	@Transactional
	public PrepareResponse go(ItemRepository itemRepository ) {

		Item item = new Item();
		item.setDescription( "36 In Red Wagon");
		item.setSummaryId( "W-001");
		item.setUnitCost( 0.0 );
		itemRepository.save( item );
		
		item = new Item();
		item.setDescription( "Painted Wagon Body");
		item.setSummaryId( "W-002");
		item.setUnitCost( 2.0 );
		itemRepository.save( item );
		
		item = new Item();
		item.setDescription( "Front Wheel Assembly");
		item.setSummaryId( "W-003");
		item.setUnitCost( 5.0 );
		itemRepository.save( item );
		
		item = new Item();
		item.setDescription( "Unpainted Wagon Body");
		item.setSummaryId( "W-018");
		item.setUnitCost( 2.0 );
		itemRepository.save( item );
		
		item = new Item();
		item.setDescription( "Red Paint");
		item.setSummaryId( "W-019");
		item.setUnitCost( 0.25 );
		itemRepository.save( item );

		return new PrepareResponse( "Item", 5 );
		
	}
	
	public List<Item> show( ItemRepository itemRepository ) {
		return itemRepository.findAll();
	}
	
}
