package com.inman.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inman.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	Item findById( Long id  );
	Item findBySummaryId( String SummaryId );
}
