package com.inman.integration;

import com.inman.service.OrderLineItemService;
import com.inman.entity.ActivityState;
import com.inman.entity.OrderLineItem;
import com.inman.repository.DdlRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;

public class DdlRepositoryTest {

    static OrderLineItem oldValue = new OrderLineItem();
    static OrderLineItem newValue = new OrderLineItem();

    OrderLineItemService orderLineItemService = new OrderLineItemService(null, null, null  );


    @BeforeEach
    public void prepare() {
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setActivityState( ActivityState.CHANGE );

    }


    @Test
    public void ddlNoChange() {
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setActivityState( ActivityState.CHANGE );

        newValue.setItemId( 17 );
        newValue.setQuantityOrdered( 10 );
        newValue.setQuantityAssigned( 0 );
        newValue.setCompleteDate( "" );
        newValue.setStartDate( "2025-0620" );
        newValue.setParentOliId( 0 );
        newValue.setActivityState( ActivityState.CHANGE );

        var fieldsToChange = orderLineItemService.createMapOfChangedValues( oldValue, newValue );
        assertEquals( 0, fieldsToChange.size() );
    }

    @Test
    public void ddlChangeQuantityOrdered() {
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setActivityState( ActivityState.CHANGE );

        newValue.setItemId( 17 );
        newValue.setQuantityOrdered( 20 );
        newValue.setQuantityAssigned( 0 );
        newValue.setCompleteDate( "" );
        newValue.setStartDate( "2025-0620" );
        newValue.setParentOliId( 0 );
        newValue.setActivityState( ActivityState.CHANGE );

        var fieldsToChange = orderLineItemService.createMapOfChangedValues( oldValue, newValue );
        assertEquals( 1, fieldsToChange.size() );

        var actualSqlString = DdlRepository.createUpdateByRowIdStatement( "OrderLineItem", 17L,
                fieldsToChange );
        var expectedSqlString = "UPDATE OrderLineItem SET quantityOrdered=20.0  WHERE id=17";
        assertEquals( expectedSqlString, actualSqlString );
    }

    @Test
    public void ddlChangeQuantityOrderedCompleteDate() {
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setActivityState( ActivityState.CHANGE );

        newValue.setItemId( 17 );
        newValue.setQuantityOrdered( 20 );
        newValue.setQuantityAssigned( 0 );
        newValue.setCompleteDate( "2025-0705" );
        newValue.setStartDate( "2025-0620" );
        newValue.setParentOliId( 0 );
        newValue.setActivityState( ActivityState.CHANGE );

        var fieldsToChange = orderLineItemService.createMapOfChangedValues( oldValue, newValue );
        assertEquals( 2, fieldsToChange.size() );

        var actualSqlString = DdlRepository.createUpdateByRowIdStatement( "OrderLineItem", 17L,
                fieldsToChange );
        var expectedSqlString = "UPDATE OrderLineItem SET completeDate='2025-0705', quantityOrdered=20.0  WHERE id=17";
        assertEquals( expectedSqlString, actualSqlString );
    }
}
