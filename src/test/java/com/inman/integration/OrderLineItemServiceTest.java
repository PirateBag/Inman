package com.inman.integration;

import com.inman.service.OrderLineItemService;
import com.inman.entity.*;
import com.inman.model.response.ResponsePackage;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;

public class OrderLineItemServiceTest {

    static OrderLineItem oldValue = new OrderLineItem();
    static OrderLineItem newValue = new OrderLineItem();

    OrderLineItemService orderLineItemService = new OrderLineItemService(null, null, null );
    static Item testItem = new Item();

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
    public void associatedItemIsNull() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, null );
        int expectedNumberOfMessages = 1;

        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = "1    Order references Item 17 that cannot be found\n";
        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());

    }

    @Test
    public void orderHasAnUnwantedParentAndNotMAN() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        oldValue.setId( 1L );
        oldValue.setParentOliId( 86 );
        oldValue.setItemId( 17L );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setCompleteDate( "2025-0701");

        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, testItem );

        int expectedNumberOfMessages = 1;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = """
        1    Item is is not MANufactured: 0000: null,null     0.00 null   0   0
        """;

        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());
    }

    @Test
    public void positiveOrderAmountAndZeroOrderAssigned() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        oldValue.setQuantityOrdered( 0 );
        oldValue.setQuantityAssigned( 1 );
        testItem.setSourcing( Item.SOURCE_MAN );
        oldValue.setParentOliId( 0 );
        oldValue.setId( 0L );
        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, testItem );

        int expectedNumberOfMessages = 2;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = """
        1    Order Quantity    0   17       0   0.00   1.00  2025-0620  2025-0701 PLANNED NONE NONE must be greater than 0.
        1    Quantity Assigned must be zero.
        """;

        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());
    }

    @Test
    public void noErrors() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        oldValue.setQuantityOrdered( 1 );
        oldValue.setQuantityAssigned( 0 );
        testItem.setSourcing( Item.SOURCE_MAN );
        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, testItem );

        int expectedNumberOfMessages = 0;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = """
        """;

        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());
    }



    @Test
    public void testMapCreateBasic() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        testItem.setSourcing( Item.SOURCE_MAN );
        newValue.setItemId( testItem.getId());
        oldValue.setItemId( testItem.getId() );

        oldValue.setQuantityAssigned( 0L );

        oldValue.setStartDate( "2025-0620" );
        oldValue.setCompleteDate( "2025-0701" );

        newValue.setStartDate( "2025-0620");
        newValue.setCompleteDate( "2025-0701");

        oldValue.setQuantityOrdered( 10 );
        newValue.setQuantityOrdered( 20 );

        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, testItem );

        int expectedNumberOfMessages = 0;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = """
        """;
        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString() );

        String actualMapString = orderLineItemService.createMapOfChangedValues( oldValue, newValue ).toString() + "\n";

        String expectedMapString = """
        {id=2, quantityOrdered=20.0}
        """;
        assertEquals( expectedMapString, actualMapString );
    }

    @Test
    public void whenIdsAreDifferent() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        oldValue.setId( 1 );
        newValue.setId( 2 );
        oldValue.setItemId( 1 );
        newValue.setItemId( testItem.getId());

        var actualMap = orderLineItemService.createMapOfChangedValues( oldValue, newValue );

        assertEquals( 2, actualMap.size() );
        assertEquals( 2L, actualMap.get( "id" ) );
    }

    @Test
    public void ddlNoChange() {
        oldValue.setId( 2 );
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
}
