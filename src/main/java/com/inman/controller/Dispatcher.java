package com.inman.controller;

import org.springframework.web.bind.annotation.RestController;

import com.inman.model.rest.StatusResponse;
import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@CrossOrigin
@RestController
public class Dispatcher {

    @RequestMapping( StatusResponse.rootUrl )
    public StatusResponse status() {
    	StatusResponse statusResponse = new StatusResponse();
    	statusResponse.setStatus( StatusResponse.INMAN_OK );
    	return statusResponse;
    }
    
    @RequestMapping( value = VerifyCredentialsRequest.rootUrl,method = RequestMethod.POST  )
    public VerifyCredentialsResponse verifyCredentials( @RequestBody VerifyCredentialsRequest request) {
    	VerifyCredentialsResponse verifyCredentialsResponse = new VerifyCredentialsResponse();
    	
    	if ( request.getUsername().equals("fred") && request.getPassword().equals("dilban") ) {
    		verifyCredentialsResponse.setStatus( StatusResponse.INMAN_OK );
    		verifyCredentialsResponse.setMessage( "logged in");
    		verifyCredentialsResponse.setToken( "78UIjk");
    	} else {
    		verifyCredentialsResponse.setStatus( StatusResponse.INMAN_OK );
    		verifyCredentialsResponse.setMessage( "Credentials not valid");
    		verifyCredentialsResponse.setToken( "none");
    	}
    	
    	return verifyCredentialsResponse;
	
    }
}

