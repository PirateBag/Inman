package com.inman.prepare;

import com.inman.entity.Item;
import com.inman.model.rest.PrepareResponse;
import com.inman.repository.ItemRepository;
import enums.SourcingType;
import org.springframework.transaction.annotation.Transactional;

public class ItemPrepare {

	public static Item w001;
	public static Item w002;
	public static Item w003;
	public static Item w004;
	public static Item w005;
	public static Item w006;
	public static Item w007;

	public static void prepareArray() {
		w001 = new Item();
		w001.setId(1L);
		w001.setDescription("36 In Red Wagon");
		w001.setSummaryId("W-001");
		w001.setSourcing( SourcingType.MAN );
		w001.setUnitCost(0.0);


		w002 = new Item();
		w002.setId(2L);
		w002.setDescription("Painted Wagon Body");
		w002.setSummaryId("W-002");
		w002.setSourcing( SourcingType.MAN );
		w002.setUnitCost(2.0);

		w003 = new Item();
		w003.setId(3L);
		w003.setDescription("Front Wheel Assembly");
		w003.setSummaryId("W-003");
		w003.setSourcing( SourcingType.MAN );
		w003.setUnitCost(5.0);


		w004 = new Item();
		w004.setId(4L);
		w004.setDescription("Painted Black Handle");
		w004.setSummaryId("W-004");
		w004.setUnitCost(1.0);
		w004.setSourcing( SourcingType.PUR );


		w005 = new Item();
		w005.setId(5L);
		w005.setDescription("Steering Assembly");
		w005.setSummaryId("W-005");
		w005.setSourcing( SourcingType.PUR );
		w005.setUnitCost(1.0);


		w006 = new Item();
		w006.setId(6L);
		w006.setDescription("Unpainted Wagon Body");
		w006.setSummaryId("W-018");
		w006.setSourcing( SourcingType.PUR);
		w006.setUnitCost(2.0);


		w007 = new Item();
		w007.setId(7L);
		w007.setDescription("Red Paint");
		w007.setSummaryId("W-019");
		w007.setSourcing( SourcingType.PUR );
		w007.setUnitCost(0.25);
	}

	@Transactional
	public PrepareResponse go(ItemRepository itemRepository ) {

		ItemPrepare.prepareArray();

		itemRepository.save( w001 );
		itemRepository.save( w002 );
		itemRepository.save( w003 );
		itemRepository.save( w004 );
		itemRepository.save( w005 );
		itemRepository.save( w006 );
		itemRepository.save( w007 );

		return new PrepareResponse( "Item", 7 );
	}
}
