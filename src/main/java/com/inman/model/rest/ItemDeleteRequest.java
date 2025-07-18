package com.inman.model.rest;

import com.inman.service.Message;
import com.inman.service.QueryParameterException;

public class ItemDeleteRequest {
	public static final String deleteUrl = "item/delete";
	private static int numberOfExpectedParameters = 1;
	
	long id;
	
	
	public ItemDeleteRequest( String id ) throws QueryParameterException {
		int numberOfParameters = 0;

		if ( id  == null || id.trim().length() == 0 ) {
			this.id = 0;
		} else {
			this.id = Long.valueOf( id.trim() );
			numberOfParameters++;
		}

		if ( numberOfParameters != numberOfExpectedParameters ) {
			throw new QueryParameterException( String.format( Message.WRONG_NUMBER_OF_PARAMETERS, 2, numberOfExpectedParameters ) );
		}
	}
	
	public long getId() {
		return id;
	}
	public void setId( long id) {
		this.id = id;
	}
}
