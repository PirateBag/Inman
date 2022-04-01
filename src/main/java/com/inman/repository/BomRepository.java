package com.inman.repository;

import com.inman.entity.Bom;
import com.inman.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BomRepository extends JpaRepository<Bom, Long> {

	List<Bom> findAll( );
	Optional<Bom> findById(Long xId );

	@Query( "select b from Bom b where b.parentId = :parentId")
	List<Bom> findByParent(
			@Param( "parentId" ) Optional<Long> parentId );
	}
