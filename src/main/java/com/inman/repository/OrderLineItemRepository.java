package com.inman.repository;

import com.inman.entity.OrderLineItem;
import com.inman.entity.OrderState;
import com.inman.entity.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Long> {

    List<OrderLineItem> findByParentOliId(long id);

    List<OrderLineItem> findByItemId(long id);

    List<OrderLineItem> findByItemIdAndOrderStateOrderByCompleteDate(long id, OrderState orderState);


}
