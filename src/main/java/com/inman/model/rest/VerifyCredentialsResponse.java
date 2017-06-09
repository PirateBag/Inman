package com.inman.model.rest;

public class VerifyCredentialsResponse {
	private String token;
	private String status;
	
	public final static String CREDENTIALS_NOT_VALID = "Credentials not valid";
	public final static String CREDENTIALS_VALID = "Credentials valid";
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
