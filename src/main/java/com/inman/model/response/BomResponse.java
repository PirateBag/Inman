package com.inman.model.response;

import com.inman.entity.Bom;

public class BomResponse extends ResponsePackage<Bom> {
    public BomResponse() {};

    public BomResponse( ResponseType xResponseType ) {
        responseType = xResponseType;
    }

    public BomResponse( ResponsePackage xResponsePackage ) {
        //   this.setData((Item[]) xResponsePackage.getData());
        Bom[] newData = new Bom[ xResponsePackage.getData().length ];
        int newDataIndex = 0;
        for( Object  item : xResponsePackage.getData() ) {
            newData[ newDataIndex++ ] = (Bom) item;
        }
        this.setData( newData );

        this.setErrors( xResponsePackage.getErrors() );
        this.responseType = xResponsePackage.getResponseType();
    }
};
