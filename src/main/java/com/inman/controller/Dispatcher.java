package com.inman.controller;

import org.springframework.web.bind.annotation.RestController;

import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@RestController
public class Dispatcher {

	@CrossOrigin
    @RequestMapping( StatusResponse.rootUrl )
    public StatusResponse status() {
    	StatusResponse statusResponse = new StatusResponse();
    	statusResponse.setStatus( StatusResponse.INMAN_OK );
    	return statusResponse;
    }
    
    @CrossOrigin
    @RequestMapping( value = VerifyCredentialsRequest.rootUrl,method = RequestMethod.POST,
    		consumes = "application/json",
    		produces = "application/json" )
    public VerifyCredentialsResponse verifyCredentials( @RequestBody VerifyCredentialsRequest request) {
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
    	
    	return verifyCredentialsResponse;
	
    }
}

