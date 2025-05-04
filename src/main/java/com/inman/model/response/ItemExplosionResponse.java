package com.inman.model.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inman.entity.Text;

public class ItemExplosionResponse extends ResponsePackage<Text> {
    public ItemExplosionResponse() {};

    public ItemExplosionResponse( String [] messages )
    {

        for ( String message : messages ) {
            getData().add( new Text( message ) );
        }
    }
};
