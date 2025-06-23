package com.inman.integration;

import com.inman.business.OrderLineItemService;
import com.inman.entity.ActivityState;
import com.inman.entity.DebitCreditIndicator;
import com.inman.entity.Item;
import com.inman.entity.OrderLineItem;
import com.inman.model.response.ResponsePackage;
import com.inman.repository.DdlRepository;
import com.inman.repository.ItemRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;

public class DdlRepositoryTest {

    static OrderLineItem oldValue = new OrderLineItem();
    static OrderLineItem newValue = new OrderLineItem();

    OrderLineItemService orderLineItemService = new OrderLineItemService((ItemRepository) null, null );
    static Item testItem = new Item();

    @BeforeEach
    public void prepare() {
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        oldValue.setActivityState( ActivityState.CHANGE );

    }




    @Test
    public void ddlNoChange() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        oldValue.setActivityState( ActivityState.CHANGE );

        newValue.setItemId( 17 );
        newValue.setQuantityOrdered( 10 );
        newValue.setQuantityAssigned( 0 );
        newValue.setCompleteDate( "" );
        newValue.setStartDate( "2025-0620" );
        newValue.setParentOliId( 0 );
        newValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        newValue.setActivityState( ActivityState.CHANGE );

        var fieldsToChange = orderLineItemService.createMapFromOldAndNew( oldValue, newValue, responsePackage );
        assertEquals( 0, fieldsToChange.size() );
    }

    @Test
    public void ddlChangeQuantityOrdered() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        oldValue.setActivityState( ActivityState.CHANGE );

        newValue.setItemId( 17 );
        newValue.setQuantityOrdered( 20 );
        newValue.setQuantityAssigned( 0 );
        newValue.setCompleteDate( "" );
        newValue.setStartDate( "2025-0620" );
        newValue.setParentOliId( 0 );
        newValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        newValue.setActivityState( ActivityState.CHANGE );

        var fieldsToChange = orderLineItemService.createMapFromOldAndNew( oldValue, newValue, responsePackage );
        assertEquals( 1, fieldsToChange.size() );

        var actualSqlString = DdlRepository.createUpdateByRowIdStatement( "OrderLineItem", 17L,
                fieldsToChange );
        var expectedSqlString = "UPDATE OrderLineItem SET QuantityOrders=20.0  WHERE id=17";
        assertEquals( expectedSqlString, actualSqlString );
    }

    @Test
    public void ddlChangeQuantityOrderedCompleteDate() {
        ResponsePackage<OrderLineItem> responsePackage = new ResponsePackage<>();
        oldValue.setItemId( 17 );
        oldValue.setQuantityOrdered( 10 );
        oldValue.setQuantityAssigned( 0 );
        oldValue.setCompleteDate( "" );
        oldValue.setStartDate( "2025-0620" );
        oldValue.setParentOliId( 0 );
        oldValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        oldValue.setActivityState( ActivityState.CHANGE );

        newValue.setItemId( 17 );
        newValue.setQuantityOrdered( 20 );
        newValue.setQuantityAssigned( 0 );
        newValue.setCompleteDate( "2025-0705" );
        newValue.setStartDate( "2025-0620" );
        newValue.setParentOliId( 0 );
        newValue.setDebitCreditIndicator( DebitCreditIndicator.ADDS_TO_BALANCE );
        newValue.setActivityState( ActivityState.CHANGE );

        var fieldsToChange = orderLineItemService.createMapFromOldAndNew( oldValue, newValue, responsePackage );
        assertEquals( 2, fieldsToChange.size() );

        var actualSqlString = DdlRepository.createUpdateByRowIdStatement( "OrderLineItem", 17L,
                fieldsToChange );
        var expectedSqlString = "UPDATE OrderLineItem SET CompletedDate='2025-0705', QuantityOrders=20.0  WHERE id=17";
        assertEquals( expectedSqlString, actualSqlString );
    }
}
