package com.inman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inman.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	Item findById( Long id  );
	Item findBySummaryId( String SummaryId );
	
	@Query( "select i from Item i where summaryId like :summaryId")
	Item[] byleadingSummaryId(
		@Param( "summaryId" ) String xSummaryId );
}
