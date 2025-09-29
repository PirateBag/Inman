package com.inman.controller;

import com.inman.model.rest.VerifyCredentialsRequest;
import com.inman.model.rest.VerifyCredentialsResponse;
import com.inman.service.VerifyCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Configuration
@RestController
public class VerifyCredentialsController {
	static Logger logger = LoggerFactory.getLogger( VerifyCredentialsController.class);

	private VerifyCredentialsService verifyCredentialsService;

	@Autowired
	VerifyCredentialsController( VerifyCredentialsService verifyCredentialsService ) {
		this.verifyCredentialsService = verifyCredentialsService;
	}


    @CrossOrigin
    @RequestMapping( value = VerifyCredentialsRequest.rootUrl,method = RequestMethod.POST,
    		consumes = "application/json",
    		produces = "application/json" )
    public ResponseEntity<VerifyCredentialsResponse> verifyCredentials(@RequestBody VerifyCredentialsRequest request) {
    	VerifyCredentialsResponse verifyCredentialsResponse = verifyCredentialsService.handle( request );

		if ( verifyCredentialsResponse.status().equals( VerifyCredentialsResponse.NO_TOKEN ) ) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( verifyCredentialsResponse );
		}
		return ResponseEntity.ok().body( verifyCredentialsResponse );
    }
}

