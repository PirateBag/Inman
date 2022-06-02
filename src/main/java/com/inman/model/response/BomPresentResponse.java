package com.inman.model.response;

import com.inman.entity.BomPresent;

public class BomPresentResponse extends ResponsePackage<BomPresent> {
    public BomPresentResponse() {};

    public BomPresentResponse(ResponseType xResponseType ) {
        responseType = xResponseType;
    }

    public BomPresentResponse(ResponsePackage xResponsePackage ) {
        //   this.setData((Item[]) xResponsePackage.getData());
        BomPresent[] newData = new BomPresent[ xResponsePackage.getData().length ];
        int newDataIndex = 0;
        for( Object  object  : xResponsePackage.getData() ) {
            newData[ newDataIndex++ ] = (BomPresent) object;
        }
        this.setData( newData );

        this.setErrors( xResponsePackage.getErrors() );
        this.responseType = xResponsePackage.getResponseType();
    }
};
