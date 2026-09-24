package com.inman.entity;

public class Credential extends EntityMaster {
    // Use private instead of public to hide the field within class
    private static final String toStringFormat = "%s %s";

    private String userName;
    private String password;

    public Credential( String userName, String password ) {
        this.userName = userName;
        this.password = password;
    }

    public Credential() {
    }

    @Override
    public EntityMaster copy(EntityMaster oldValue) {
        return null;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
