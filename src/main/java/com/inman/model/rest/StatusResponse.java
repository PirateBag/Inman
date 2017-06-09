package com.inman.model.rest;

public class StatusResponse {
	public final static String INMAN_OK = "Inman Ok";
	public final static String INMAN_FAIL = "Inman Fail";
	public final static String rootUrl = "/status";
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
