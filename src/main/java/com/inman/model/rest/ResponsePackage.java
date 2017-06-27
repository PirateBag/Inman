package com.inman.model.rest;

import org.springframework.http.HttpStatus;

public class ResponsePackage<T> {
	HttpStatus httpStatus;
	Errors[] errors;
	T[] data; 

	public ResponsePackage<T>( HttpStatus httpStatus, Errors[] error, T data) {
		this.httpStatus = httpStatus;
		this.errors = errors;
		this.data = data;
	}
	
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	public Errors[] getErrors() {
		return errors;
	}
	public void setErrors(Errors[] errors) {
		this.errors = errors;
	}
	public T[] getData() {
		return data;
	}
	public void setData(T[] data) {
		this.data = data;
	}
}
