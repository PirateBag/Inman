package com.inman.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inman.entity.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

	Item findById( long id  );
	Item findBySummaryId( String SummaryId );
	Item deleteBySummaryId( String SummaryId );

	@Override
	@NotNull
	List<Item> findAll();

	@NotNull
	List<Item> findAllByOrderBySummaryIdAsc();

	@Query( "select i from Item i where summaryId like :summaryId")
	Item[] byleadingSummaryId(
		@Param( "summaryId" ) String xSummaryId );
	
	@Query( "select i from Item i where description like :description")
	Item[] byDescription(
			@Param( "description" ) String description);

	@Query( "select i.id, i.summaryId, i.description from Item i")
	Item[] pickList();

	@Query( "select b.parentId from Item i,Bom b where b.childId=i.id and i.id = :itemId")
	long[] findParentsFor( @Param( "itemId") long id );

	//  Delete all as a transaction.
	void deleteAllInBatch();

	@Query( "select i from Item i, OrderLineItem  oli where oli.itemId = i.id order by i.maxDepth, i.id, oli.completeDate ")
	Item[] itemsByDepthAndId();

	List<Item> findAllByOrderByMaxDepthAsc();
}
