package com.inman.integration;

import com.inman.controller.Application;
import com.inman.entity.Adjustment;
import com.inman.model.request.AdjustmentCrudRequest;
import com.inman.repository.AdjustmentRepository;
import com.inman.service.AdjustmentService;
import enums.AdjustmentType;
import enums.OrderType;
import enums.CrudAction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@Transactional
public class AdjustmentQueryTest {

    @Autowired
    private AdjustmentService adjustmentService;

    @Autowired
    private AdjustmentRepository adjustmentRepository;

    @Test
    public void testQueryAdjustments() {
        // Setup some data
        Adjustment adj1 = new Adjustment();
        adj1.setAmount(10.0);
        adj1.setItemId(1L);
        adj1.setOrderId(101L);
        adj1.setOrderType(OrderType.PO);
        adj1.setEffectiveDate("2023-0101");
        adj1.setAdjustmentType(AdjustmentType.XFER);
        
        Adjustment adj2 = new Adjustment();
        adj2.setAmount(20.0);
        adj2.setItemId(2L);
        adj2.setOrderId(102L);
        adj2.setOrderType(OrderType.PO);
        adj2.setEffectiveDate("2023-0101");
        adj2.setAdjustmentType(AdjustmentType.XFER);
        
        adjustmentRepository.save(adj1);
        adjustmentRepository.save(adj2);

        // Test Query 1: Find by itemId 1
        Adjustment filter1 = new Adjustment();
        filter1.setItemId(1L);
        AdjustmentCrudRequest request = new AdjustmentCrudRequest(new Adjustment[]{filter1});

        Collection<Adjustment> results = adjustmentService.query(request);
        assertEquals(1, results.size());
        assertEquals(1L, results.iterator().next().getItemId());

        String sql = adjustmentService.getLastQuerySql();
        assertNotNull(sql);
        assertTrue(sql.contains("itemId"), "SQL did not contain itemId: " + sql);

        // Test Query 2: Find by (itemId 1 AND amount 10) OR (itemId 2 AND amount 20)
        Adjustment filter2a = new Adjustment();
        filter2a.setItemId(1L);
        filter2a.setAmount(10.0);
        
        Adjustment filter2b = new Adjustment();
        filter2b.setItemId(2L);
        filter2b.setAmount(20.0);
        
        request = new AdjustmentCrudRequest(new Adjustment[]{filter2a, filter2b});
        results = adjustmentService.query(request);
        assertEquals(2, results.size());
        
        sql = adjustmentService.getLastQuerySql();
        assertTrue(sql.contains("itemId"));
        assertTrue(sql.contains("amount"));
    }

    @Test
    public void testQueryAll() {
        AdjustmentCrudRequest request = new AdjustmentCrudRequest(new Adjustment[0]);
        Collection<Adjustment> results = adjustmentService.query(request);
        assertNotNull(results);
    }

    @Test
    public void testQueryNullThrows() {
        AdjustmentCrudRequest request = new AdjustmentCrudRequest(null);
        assertThrows(IllegalArgumentException.class, () -> adjustmentService.query(request));
    }
}
