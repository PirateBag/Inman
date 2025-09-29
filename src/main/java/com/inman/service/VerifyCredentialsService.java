package com.inman.service;

import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VerifyCredentialsService {
	static Logger logger = LoggerFactory.getLogger( VerifyCredentialsService.class );
	public VerifyCredentialsResponse handle( VerifyCredentialsRequest request ) {

	VerifyCredentialsResponse verifyCredentialsResponse;

	if ( request.userName().equals("fred") && request.password().equals("dilban") ) {
		verifyCredentialsResponse = new VerifyCredentialsResponse( VerifyCredentialsResponse.DEFAULT_TOKEN,
				VerifyCredentialsResponse.DEFAULT_TOKEN );
		logger.info(STR."User \{request.userName()} has been credentialed.");
	} else {
		verifyCredentialsResponse = new VerifyCredentialsResponse( VerifyCredentialsResponse.NO_TOKEN,
				VerifyCredentialsResponse.NO_TOKEN );
		logger.info( "User " + request.userName() + " failed to provide valid credentials.");
	}

	return verifyCredentialsResponse;
	}
}
