package com.inman.model.rest;

public class VerifyCredentialsRequest {
	
	public static final String rootUrl = "verifyCredentials";


	private String username;
	private String password;

	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
}
