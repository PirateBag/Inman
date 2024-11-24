package com.inman.entity;

public class Text extends EntityMaster{
    private String message;

    public Text() {};

    public Text(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
    public void setMessage(String text) {
        this.message = text;
    }

    @Override
    public EntityMaster copy(EntityMaster oldValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
