package com.inman.model.response;

import com.inman.entity.Text;
import org.slf4j.Logger;

import java.util.Optional;

public class TextResponse extends ResponsePackage<Text> {
    public TextResponse() {};

    public TextResponse(String [] messages )
    {
        long id =  1;
        for ( String message : messages ) {
            getData().add( new Text( message, id++ ) );
        }
    }

    public void addText( String text, Optional<Logger> logger ) {
        getData().add( new Text( text ) );
        if ( logger.isPresent() ) {
            logger.get().info( text );
        }
    }

};
