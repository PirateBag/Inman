package com.inman.model.response;

import com.inman.entity.Item;

public class ItemResponse extends ResponsePackage<Item> {
    public ItemResponse() {};

    public ItemResponse( ResponseType xResponseType ) {
        responseType = xResponseType;
    }

    /*
    public ItemResponse( ResponsePackage xResponsePackage ) {

        //   this.setData((Item[]) xResponsePackage.getData());
        Item[] newData = new Item[ xResponsePackage.getData().length ];
        int newDataIndex = 0;
        for( Object  item : xResponsePackage.getData() ) {
            newData[ newDataIndex++ ] = (Item) item;
        }
        this.setData( newData );

        this.setErrors( xResponsePackage.getErrors() );
        this.responseType = xResponsePackage.getResponseType();
    }  */
};
