package com.inman.model.response;

import com.inman.entity.Pick;

import java.util.List;

public class ItemPickListResponse extends ResponsePackage<Pick> {
    public ItemPickListResponse() {};

    public ItemPickListResponse(ResponseType xResponseType ) {
        responseType = xResponseType;
    }

    /*
    public ItemPickListResponse(ResponsePackage xResponsePackage ) {

        //   this.setData((Item[]) xResponsePackage.getData());
        Pick[] newData = new Pick[ xResponsePackage.getData().length ];
        int newDataIndex = 0;
        for( Object  itemPickList : xResponsePackage.getData() ) {
            newData[ newDataIndex++ ] = (Pick) itemPickList;
        }
        this.setData( newData );

        this.setErrors( xResponsePackage.getErrors() );
        this.responseType = xResponsePackage.getResponseType();
    } */
};
