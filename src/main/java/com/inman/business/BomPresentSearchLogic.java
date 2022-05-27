package com.inman.business;

import org.springframework.stereotype.Service;

@Service
public class BomPresentSearchLogic {
/*
	public BomPresent[] byAll(BomPresentRepository xBomPresentRepository ) {
		BomPresent[] boms = xBomPresentRepository.findAllBom();
		return boms;  //  .toArray(new BomPresent[boms.size() ]);
	}

	public BomPresent[] findByParentId(BomPresentRepository xBomRepository, long xId  ) {
		BomPresent[] boms = xBomRepository.byParentId( xId );
		return boms;
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
