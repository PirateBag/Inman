package com.inman.integration;

import com.inman.business.ReflectionHelpers;
import com.inman.entity.ActivityState;
import com.inman.entity.OrderLineItem;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;


import java.util.Map;

import static com.inman.business.ReflectionHelpers.compareObjects;
import static org.junit.Assert.assertEquals;

public class CompareObjecftsTest {

    static OrderLineItem oldValue = new OrderLineItem();
    static OrderLineItem newValue = new OrderLineItem();

    private static String mapCompare( Map<String,Object[]> resultOfCompare ) {
        StringBuilder result = new StringBuilder( "Field  old  new " );
        int count = 0;
        for( Map.Entry<String,Object[]> entry : resultOfCompare.entrySet() ) {
            String key = entry.getKey();
            Object[] value = entry.getValue();
            String format = "%3d %-15s: '%s' != '%s'";

            String line = String.format( format, count, key, value[ 0 ],  value[ 1 ] );
            result.append( line );
            count++;
        }
        return result.toString();
    }

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
    public void whenNoChange() {
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

        var fieldsToChange = compareObjects( oldValue, newValue );
        assertEquals( 0, fieldsToChange.size() );

        var results = mapCompare( fieldsToChange );
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

        var fieldsToChange = compareObjects( oldValue, newValue );
        assertEquals( 1, fieldsToChange.size() );
        var results = mapCompare( fieldsToChange );


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

        var fieldsToChange = compareObjects( oldValue, newValue );
        assertEquals( 2, fieldsToChange.size() );
        var results = mapCompare( fieldsToChange );
//        var actualSqlString = DdlRepository.createUpdateByRowIdStatement( "OrderLineItem", 17L,
                //  fieldsToChange );
        var expectedSqlString = "UPDATE OrderLineItem SET CompletedDate='2025-0705', QuantityOrders=20.0  WHERE id=17";
  //      assertEquals( expectedSqlString, actualSqlString );
    }

    @Test
    public void setOfFields() {
        var setOfFields = ReflectionHelpers.setOfFields( newValue );

        assertEquals( 10, setOfFields.size() );

        String expected = """
                activityState
                completeDate
                id
                itemId
                orderState
                orderType
                parentOliId
                quantityAssigned
                quantityOrdered
                startDate
                """;

        StringBuffer actual = new StringBuffer();

        for ( String key : setOfFields ) {
            actual.append( key ).append( '\n' );
        }
        assertEquals( expected, actual.toString() );

    }
}
