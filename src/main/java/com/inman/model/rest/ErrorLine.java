package com.inman.model.rest;

public class ErrorLine {
	private int key;
	private String message;
	
	public ErrorLine( int key, String code, String message ) {
		this.key = key;
		this.message = message;
	}

	public ErrorLine( int key, String message ) {
		this.key = key;
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
	
	
}
