package com.inman.model;

import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.PagingAndSortingRepository;

@RepositoryRestResource(collectionResourceRel = "item", path = "item")
public interface ItemRepository extends PagingAndSortingRepository<Item,Long> {
	List<Item> findBySummaryId( @Param( "summaryId") String summaryId );
}
