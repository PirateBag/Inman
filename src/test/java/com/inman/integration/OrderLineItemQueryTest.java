package com.inman.integration;

import com.inman.entity.OrderLineItem;
import com.inman.model.request.OrderLineItemRequest;
import com.inman.model.response.ResponsePackage;
import com.inman.repository.OrderLineItemRepository;
import com.inman.service.OrderLineItemService;
import enums.OrderState;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderLineItemQueryTest {

    private OrderLineItemRepository orderLineItemRepository;
    private OrderLineItemService orderLineItemService;

    @BeforeEach
    public void setUp() {
        orderLineItemRepository = mock(OrderLineItemRepository.class);
        orderLineItemService = new OrderLineItemService(null, orderLineItemRepository, null);
    }

    @Test
    public void testOrderReportCrudWithFilters() {
        OrderLineItem filter = new OrderLineItem();
        filter.setItemId(100L);
        filter.setOrderState(OrderState.PLANNED);
        
        OrderLineItemRequest request = new OrderLineItemRequest(new OrderLineItem[]{filter});

        ArgumentCaptor<Specification<OrderLineItem>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(orderLineItemRepository.findAll(specCaptor.capture())).thenReturn(Collections.emptyList());

        ResponsePackage<OrderLineItem> response = orderLineItemService.orderReportCrud(request);

        assertNotNull(response);
        
        // Verify specification builds the correct predicates
        Specification<OrderLineItem> spec = specCaptor.getValue();
        Root<OrderLineItem> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        
        spec.toPredicate(root, query, cb);
        
        verify(cb).equal(root.get("itemId"), 100L);
        verify(cb).equal(root.get("orderState"), OrderState.PLANNED);
    }

    @Test
    public void testOrderReportCrudWithoutFilters() {
        OrderLineItemRequest request = new OrderLineItemRequest(new OrderLineItem[0]);

        when(orderLineItemRepository.findAll()).thenReturn(Collections.emptyList());

        ResponsePackage<OrderLineItem> response = orderLineItemService.orderReportCrud(request);

        assertNotNull(response);
        verify(orderLineItemRepository).findAll();
    }
}
