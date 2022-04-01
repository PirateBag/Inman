package com.inman.prepare;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.inman.entity.Item;
import com.inman.model.rest.PrepareResponse;
import com.inman.repository.ItemRepository;

public class ItemPrepare {

	public static Item w001;
	public static Item w002;
	public static Item w003;


	@Transactional
	public PrepareResponse go(ItemRepository itemRepository ) {

		w001 = new Item();
		w001.setDescription( "36 In Red Wagon");
		w001.setSummaryId( "W-001");
		w001.setUnitCost( 0.0 );
		itemRepository.save( w001 );

		w002 = new Item();
		w002.setDescription( "Painted Wagon Body");
		w002.setSummaryId( "W-002");
		w002.setUnitCost( 2.0 );
		itemRepository.save( w002 );

		w003 = new Item();
		w003.setDescription( "Front Wheel Assembly");
		w003.setSummaryId( "W-003");
		w003.setUnitCost( 5.0 );
		itemRepository.save( w003 );
		
		Item item = new Item();
		item.setDescription( "Painted Black Handle");
		item.setSummaryId( "W-004");
		item.setUnitCost( 1.0 );
		itemRepository.save( item );
		
		item = new Item();
		item.setDescription( "Steering Assembly");
		item.setSummaryId( "W-005");
		item.setUnitCost( 1.0 );
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

		return new PrepareResponse( "Item", 7 );
		
	}
	
	public List<Item> show( ItemRepository itemRepository ) {
		return itemRepository.findAll();
	}


}
