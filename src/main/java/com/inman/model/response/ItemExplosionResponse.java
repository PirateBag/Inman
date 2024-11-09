package com.inman.model.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.inman.entity.Text;

public class ItemExplosionResponse extends ResponsePackage<Text> {
    public ItemExplosionResponse() {};

    public ItemExplosionResponse( String [] messages )
    {

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        try {
            var json1 = ow.writeValueAsString( this );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Text [] texts = new Text[ messages.length ];

        int textIndex = 0;
        for ( String message : messages  ) {
            texts[ textIndex++ ] = new Text( message );
        }
        this.setData( texts );
    }
};
