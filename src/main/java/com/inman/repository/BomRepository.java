package com.inman.repository;

import com.inman.entity.Bom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BomRepository extends JpaRepository<Bom, Long> {

	public void deleteAllInBatch();
	/*List<BomPresent> findByParent(
			@Param( "parentId" ) Optional<Long> parentId );

	 */
	}
