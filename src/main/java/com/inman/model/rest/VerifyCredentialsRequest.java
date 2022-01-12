package com.inman.model.rest;

import java.io.Serializable;

public class VerifyCredentialsRequest  implements Serializable {
	
	public static final String rootUrl = "verifyCredentials";

	private String username;
	private String password;
	
	public VerifyCredentialsRequest() { };

	public VerifyCredentialsRequest( String xUsername, String xPassword ) {
		username = xUsername;
		password = xPassword; 
	}
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
