package com.inman.model.rest;

import java.util.ArrayList;

public class ResponsePackage<T> {
	ArrayList<ErrorLine> errors = new ArrayList<ErrorLine>();
	T[] data; 

	public ResponsePackage(  ArrayList<ErrorLine> errors, T[] data) {
		this.errors = errors;
		this.data = data;
	}
	
	
	public ResponsePackage() {
	}
	
	public void addError( ErrorLine error ) {
		errors.add( error );
	}

	public ArrayList<ErrorLine> getErrors() {
		return errors;
	}
	public void setErrors( ArrayList<ErrorLine> errors) {
		this.errors = errors;
	}
	public T[] getData() {
		return data;
	}
	public void setData(T[] data) {
		this.data = data;
	}
}
