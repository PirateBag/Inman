package com.inman.repository;

import com.inman.entity.Adjustment;
import com.inman.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdjustmentRepository extends JpaRepository<Adjustment, Long> {

	Adjustment findById(long id);

	@Override
	@NotNull
	List<Adjustment> findAll();


	//  Delete all as a transaction.
	void deleteAllInBatch();
}

