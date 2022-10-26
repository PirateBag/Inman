package com.inman.model.response;

import com.inman.entity.BomPresent;

public class BomResponse extends ResponsePackage<BomPresent> {
    public BomResponse() {};

    public BomResponse( ResponseType xResponseType, BomPresent[] xResponses ) {

        BomPresent[] newData = new BomPresent[ xResponses.length ];
        for( int index = 0; index < xResponses.length; index++  ) {
            newData[ index ] = xResponses[ index ];
        }
        this.setData( newData );
        this.responseType = xResponseType;
    }
};
