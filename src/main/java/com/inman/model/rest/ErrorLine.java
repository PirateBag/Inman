package com.inman.model.rest;

public class ErrorLine {
	public final static String NO_KEY="NoKey";
	private String key;
	private String code;
	private String message;
	
	public ErrorLine( String key, String code, String message ) {
		this.key = key;
		this.code = code;
		this.message = message;
	}
	public ErrorLine() {
		
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
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
