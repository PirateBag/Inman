package com.inman.prepare;

import com.inman.entity.Bom;
import com.inman.model.rest.PrepareResponse;
import com.inman.repository.BomRepository;
import org.springframework.transaction.annotation.Transactional;

public class BomPrepare {

	@Transactional
	public PrepareResponse  go(BomRepository xBomRepository ) {
		/*
		Bom bom = new Bom();
		bom.setParentId(1L);
		bom.setChildId(2L);
		bom.setQuantityPer( 1.0 );
		xBomRepository.save( bom );

		bom = new Bom();
		bom.setParentId(1L);
		bom.setChildId(3L);
		bom.setQuantityPer( 2.0 );
		xBomRepository.save( bom );  */
		return new PrepareResponse( "Bom", 2);
	}

}
