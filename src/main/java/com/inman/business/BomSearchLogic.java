package com.inman.business;

import com.inman.entity.Bom;
import com.inman.repository.BomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BomSearchLogic {

	public Bom[] byAll(BomRepository xBomRepository ) {
		List<Bom> boms = xBomRepository.findAll();
		return boms.toArray(new Bom[boms.size() ]);
	}
/*
	public BomPresent[] findByParentId(BomRepository xBomRepository, Long xId  ) {
		List<BomPresent> boms = xBomRepository.findByParent( Optional.of( xId ) );
		return boms.toArray(new BomPresent[boms.size() ]);
	}
/*
	public Bom[]  byId(BomRepository xBomRepository, Long xId  ) {
		Optional<Bom> bom = xBomRepository.findById( xId );

		if ( bom.isEmpty() ) {
			return new Bom[0];
		}
		Bom[] boms = new Bom[] { bom.get() };
		return boms;
	}
*/

}
