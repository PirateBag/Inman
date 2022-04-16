package com.inman.business;

import com.inman.entity.Bom;
import com.inman.repository.BomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BomSearchLogic {

	public Bom[] byAll( BomRepository xBomRepository ) {
		List<Bom> boms = xBomRepository.findAll();
		return boms.toArray(new Bom[boms.size() ]);
	}

	public Bom[] findByParentId( BomRepository xBomRepository, Long xId  ) {
		List<Bom> boms = xBomRepository.findByParent( Optional.of( xId ) );
		return boms.toArray(new Bom[boms.size() ]);
	}


	public Bom[]  byId(BomRepository xBomRepository, Long xId  ) {
		Optional<Bom> bom = xBomRepository.findById( xId );

		if ( bom.isEmpty() ) {
			return new Bom[0];
		}
		Bom[] boms = new Bom[1];
		boms[ 0 ] = bom.get();
		return boms;
	}


}