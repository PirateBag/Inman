package com.inman.integration;

import com.inman.business.EntityUtility;
import com.inman.business.OrderLineItemService;
import com.inman.entity.*;
import com.inman.model.response.ResponsePackage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SqlBuilderTest {

    static OrderLineItem oldValue = new OrderLineItem();
    static OrderLineItem newValue = new OrderLineItem();
    static ResponsePackage<OrderLineItem> responsePackage;
    static OrderLineItemService orderLineItemService;
    static Item testItem;
    @BeforeClass
    public static void prepare() {
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        oldValue.setActivityState( ActivityState.CHANGE );
        orderLineItemService= new OrderLineItemService();

         testItem = new Item();
        responsePackage = new ResponsePackage<>();
    }

    @Test
    public void associatedItemIsNull() {
        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, null );

        responsePackage.getErrors();
        int expectedNumberOfMessages = 1;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = "1    Order references Item 17 that cannot be found\n";
        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());

    }

    @Test
    public void orderHasAnUnwantedParentAndNotMAN() {
        oldValue.setParentOliId( 86 );
        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, testItem );

        responsePackage.getErrors();
        int expectedNumberOfMessages = 2;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = """
        1    Order has a parent item:     0   17      86  10.00   0.00  2025-0620            PLANNED ADDS_TO_BALANCE CHANGE
        1    Item is is not MANufactured: 0000: null,null     0.00 null   0   0
        """;

        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());
    }

    @Test
    public void positiveOrderAmountAndZeroOrderAssigned() {
        oldValue.setQuantityOrdered( 0 );
        oldValue.setQuantityAssigned( 1 );
        testItem.setSourcing( Item.SOURCE_MAN );
        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, testItem );

        int expectedNumberOfMessages = 2;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = """
        1    Order Quantity    0   17       0   0.00   1.00  2025-0620            PLANNED ADDS_TO_BALANCE CHANGE must be greater than 0.
        1    Quantity Assigned must be zero. 
        """;

        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());
    }

    @Test
    public void noErrors() {
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
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        newValue.setItemId( testItem.getId());

        oldValue.setItemId( testItem.getId() );
        testItem.setSourcing( Item.SOURCE_MAN );
        int numberOfMessages = orderLineItemService.validateOrderLineItemForMOInsertion( oldValue, responsePackage, testItem );

        int expectedNumberOfMessages = 0;
        assertEquals(expectedNumberOfMessages, numberOfMessages);
        String expectedErrorText = """
        """;
        assertEquals( expectedErrorText, responsePackage.getErrorsTextAsString());

        var expected = orderLineItemService.createMapFromOldAndNew( oldValue, newValue, responsePackage );
        String expectedMapText = """
        """;
        assertEquals( expectedMapText, responsePackage.getErrorsTextAsString());

    }

}
