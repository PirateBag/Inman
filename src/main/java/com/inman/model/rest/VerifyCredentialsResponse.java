package com.inman.model.rest;

import java.io.Serializable;

public record VerifyCredentialsResponse( String userName, String token, String status ) implements Serializable {


	public static final String NO_TOKEN = "No Token";
	public static final String DEFAULT_TOKEN = "78UIjk";


}
