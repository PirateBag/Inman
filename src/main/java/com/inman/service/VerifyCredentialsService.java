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
		verifyCredentialsResponse = new VerifyCredentialsResponse( request.userName(), VerifyCredentialsResponse.DEFAULT_TOKEN,
				VerifyCredentialsResponse.DEFAULT_TOKEN );
		logger.info("User {} has been credentialed.", request.userName());
	} else {
		verifyCredentialsResponse = new VerifyCredentialsResponse( request.userName(), VerifyCredentialsResponse.NO_TOKEN,
				VerifyCredentialsResponse.NO_TOKEN );
        logger.info("User {} failed to provide valid credentials.", request.userName());
	}

	return verifyCredentialsResponse;
	}
}
