package com.inman.business;

import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;

public class VerifyCredentialsLogic {
	
	public VerifyCredentialsResponse handle( VerifyCredentialsRequest request ) {
	VerifyCredentialsResponse verifyCredentialsResponse = new VerifyCredentialsResponse();
	
	if ( request.getUsername().equals("fred") && request.getPassword().equals("dilban") ) {
		verifyCredentialsResponse.setStatus( StatusResponse.INMAN_OK );
		verifyCredentialsResponse.setMessage( VerifyCredentialsResponse.CREDENTIALS_VALID + request.getUsername());
		verifyCredentialsResponse.setToken( VerifyCredentialsResponse.DEFAULT_TOKEN );
	} else {
		verifyCredentialsResponse.setStatus( StatusResponse.INMAN_FAIL );
		verifyCredentialsResponse.setMessage(  VerifyCredentialsResponse.CREDENTIALS_NOT_VALID );
		verifyCredentialsResponse.setToken( VerifyCredentialsResponse.NO_TOKEN );
	}
	System.out.println( verifyCredentialsResponse.getStatus() + ", " +
			verifyCredentialsResponse.getMessage() + ", " +
			verifyCredentialsResponse.getToken() );
			
	return verifyCredentialsResponse;
	}
}
