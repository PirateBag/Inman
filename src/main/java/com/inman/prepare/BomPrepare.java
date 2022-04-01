package com.inman.prepare;

import com.inman.entity.Bom;
import com.inman.entity.Item;
import com.inman.model.rest.PrepareResponse;
import com.inman.repository.BomRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class BomPrepare {

	@Transactional
	public PrepareResponse  go(BomRepository xBomRepository ) {
		xBomRepository.save( new Bom(1L, 2L, 1.0 ) );
		xBomRepository.save( new Bom( 1L, 3L, 1.0 ) );

		return new PrepareResponse( "Bom", 2);
	}
	
	public List<Bom> show( BomRepository xBomRepository ) {
		return xBomRepository.findAll();
	}


}
