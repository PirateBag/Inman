package com.inman.model.response;

import com.inman.entity.Text;

public class TextResponse extends ResponsePackage<Text> {
    public TextResponse() {};

    public TextResponse(String [] messages )
    {

        for ( String message : messages ) {
            getData().add( new Text( message ) );
        }
    }
};
