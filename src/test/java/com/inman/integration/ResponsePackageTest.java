package com.inman.integration;

import com.inman.entity.Item;
import com.inman.model.response.ItemResponse;
import com.inman.model.response.ResponsePackage;
import com.inman.model.response.ResponseType;
import com.inman.prepare.ItemPrepare;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class ResponsePackageTest {
    ItemResponse itemResponse = new ItemResponse( ResponseType.QUERY );
    Item[] queryItems;

    static <T> T[] concatWithArrayCopy(T[] array1, T[] array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    static boolean areItemArraysEqual(Object[] actual, Item[] exepcted ) {
        if (actual.length != exepcted.length ) { return false; };

        for ( int i = 0 ; i < actual.length ; i++ ) {
            if ( actual[ i ] != exepcted[ i ]) { return false; }
        }
        return true;
    }

    @Before
    public void setUp() {
        ItemPrepare.prepareArray();
        queryItems =  new Item[] { ItemPrepare.w001, ItemPrepare.w003, ItemPrepare.w005 };
        itemResponse.setData( queryItems );
    }

    @Test(expected = IllegalArgumentException.class )
    public void addMergeTestWrongType() {

        ItemResponse deltaResponse = new ItemResponse( ResponseType.QUERY );
        Item[] deltaItems = { ItemPrepare.w001 };

        var actualValue = itemResponse.mergeAnotherResponse( deltaResponse );
    }

    @Test
    public void mergeAnItem() {
        ItemResponse deltaResponse = new ItemResponse( ResponseType.ADD );
        Item[] deltaItems = { ItemPrepare.w002 };
        deltaResponse.setData( deltaItems );

        Item[] expected = concatWithArrayCopy( queryItems, deltaItems );

        ResponsePackage actual =itemResponse.mergeAnotherResponse( deltaResponse );
        assertTrue( areItemArraysEqual((Item[]) actual.getData(), expected ) );
    }

    @Test
    public void mergeTwoItem() {
        ItemResponse deltaResponse = new ItemResponse( ResponseType.ADD );
        Item[] deltaItems = { ItemPrepare.w002, ItemPrepare.w004 };
        deltaResponse.setData( deltaItems );

        Item[] expected = concatWithArrayCopy( queryItems, deltaItems );

        ResponsePackage actual =itemResponse.mergeAnotherResponse( deltaResponse );
        assertTrue( areItemArraysEqual((Item[]) actual.getData(), expected ) );
    }

    @Test
    public void changeAnItem() {
        ItemResponse deltaResponse = new ItemResponse( ResponseType.CHANGE );
        Item w003b = new Item();
        w003b.setId( 3L );
        w003b.setSummaryId( "W-003B");
        w003b.setDescription( "W-003b Desc" );
        w003b.setUnitCost( 0.111 );

        Item[] deltaItems = { w003b };
        deltaResponse.setData( deltaItems );

        Item[] expected = new Item[] { ItemPrepare.w001, w003b, ItemPrepare.w005 };

        ResponsePackage actual =itemResponse.mergeAnotherResponse( deltaResponse );
        assertTrue( areItemArraysEqual( actual.getData(), expected ) );
    }

    @Test(expected = IllegalStateException.class )
    public void deleteTwoItemsButOneDoesntExist() {
        ItemResponse deltaResponse = new ItemResponse( ResponseType.DELETE );

        Item[] deltaItems = { ItemPrepare.w001, ItemPrepare.w002 };
        deltaResponse.setData( deltaItems );

        Item[] expected = new Item[] { ItemPrepare.w003, ItemPrepare.w005 };

        ResponsePackage actual =itemResponse.mergeAnotherResponse( deltaResponse );
        assertTrue( areItemArraysEqual( actual.getData(), expected ) );
    }

    @Test
    public void deleteOneItem() {
        ItemResponse deltaResponse = new ItemResponse( ResponseType.DELETE );

        Item[] deltaItems = { ItemPrepare.w001 };
        deltaResponse.setData( deltaItems );

        Item[] expected = new Item[] { ItemPrepare.w003, ItemPrepare.w005 };

        ResponsePackage actual =itemResponse.mergeAnotherResponse( deltaResponse );
        assertTrue( areItemArraysEqual( actual.getData(), expected ) );
    }


}
