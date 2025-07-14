package com.inman.repository;

import com.inman.entity.OrderLineItem;
import com.inman.entity.OrderState;
import com.inman.entity.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {

    List<OrderLineItem> findAll();

    List<OrderLineItem> findByParentOliId(long id);

    List<OrderLineItem> findByItemId(long id);

    List<OrderLineItem> findByItemIdAndOrderStateOrderByCompleteDate(long id, OrderState orderState);

    @Query( "select oli from OrderLineItem oli order by oli.itemId, oli.completeDate" )
    List<OrderLineItem>  getOliOrderByItemIdAndCompleteDate();

 }
