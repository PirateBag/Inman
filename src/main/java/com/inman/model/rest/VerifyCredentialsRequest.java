package com.inman.model.rest;

import java.io.Serializable;

public record VerifyCredentialsRequest( String userName, String password )  implements Serializable {
	public static final String rootUrl = "verifyCredentials";
}
