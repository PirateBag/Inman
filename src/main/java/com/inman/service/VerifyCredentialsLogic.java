package com.inman.service;

import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyCredentialsLogic {
	static Logger logger = LoggerFactory.getLogger( "business: " + VerifyCredentialsLogic.class );
	public VerifyCredentialsResponse handle( VerifyCredentialsRequest request ) {
	VerifyCredentialsResponse verifyCredentialsResponse = new VerifyCredentialsResponse();
	
	if ( request.getUsername().equals("fred") && request.getPassword().equals("dilban") ) {
		verifyCredentialsResponse.setStatus( StatusResponse.INMAN_OK );
		verifyCredentialsResponse.setMessage( VerifyCredentialsResponse.CREDENTIALS_VALID + request.getUsername());
		verifyCredentialsResponse.setToken( VerifyCredentialsResponse.DEFAULT_TOKEN );
		logger.info( "User " + request.getUsername() + " has been credentialed.");
	} else {
		verifyCredentialsResponse.setStatus( StatusResponse.INMAN_FAIL );
		verifyCredentialsResponse.setMessage(  VerifyCredentialsResponse.CREDENTIALS_NOT_VALID );
		verifyCredentialsResponse.setToken( VerifyCredentialsResponse.NO_TOKEN );
		logger.info( "User " + request.getUsername() + " failed to provide valid credentials.");
	}

	return verifyCredentialsResponse;
	}
}
