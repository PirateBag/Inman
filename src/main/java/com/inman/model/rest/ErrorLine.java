package com.inman.model.rest;

public class ErrorLine {
	private int key;
	private String code;
	private String message;
	
	public ErrorLine( int key, String code, String message ) {
		this.key = key;
		this.code = code;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
