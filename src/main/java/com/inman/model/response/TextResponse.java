package com.inman.model.response;

import com.inman.entity.Text;

import java.util.Optional;
import org.slf4j.Logger;

public class TextResponse extends ResponsePackage<Text> {
    public TextResponse() {};

    public TextResponse(String [] messages )
    {

        for ( String message : messages ) {
            getData().add( new Text( message ) );
        }
    }

    public void addText( String text, Optional<Logger> logger ) {
        getData().add( new Text( text ) );
        if ( logger.isPresent() ) {
            logger.get().info( text );
        }
    }

};
