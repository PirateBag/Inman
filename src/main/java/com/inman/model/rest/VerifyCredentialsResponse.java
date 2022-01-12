package com.inman.model.rest;

import java.io.Serializable;

public class VerifyCredentialsResponse implements Serializable {

	public static final String NO_TOKEN = "No Token";
	public static final String DEFAULT_TOKEN = "78UIjk";

	private String token;
	private String status;
	
	public static final String CREDENTIALS_NOT_VALID = "Credentials not valid";
	public static final String CREDENTIALS_VALID = "You are logged in as ";
	
	private String message;
	
	public String getToken() {
		
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	
}
