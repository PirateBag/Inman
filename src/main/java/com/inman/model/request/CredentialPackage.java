package com.inman.model.request;

import com.inman.entity.Credential;


public record CredentialPackage(Credential[] rows ) {
    public static final String rootUrl = "credentials";
}
