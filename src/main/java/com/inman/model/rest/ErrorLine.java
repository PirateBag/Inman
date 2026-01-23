package com.inman.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorLine {
	private int key;
	private String message;
    private HttpStatus status;

    public HttpStatus getStatus() {
        return status;
    }

	public ErrorLine( int key, String code, String message ) {
		this.key = key;
		this.message = message;
	}

	public ErrorLine( int key, String message ) {
		this.key = key;
		this.message = message;
	}

    public ErrorLine( HttpStatus status, String message ) {
        this.status = status;
        this.message = message;
    }

	public ErrorLine() {
		
	}
	
	public int  getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
        return "%d %s".formatted(key, message);
    }
}
