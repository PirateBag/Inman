package com.inman.model.rest;

public class StatusResponse {
	public final static String INMAN_OK = "online and available.";
	public final static String INMAN_FAIL = "off line.";
	public final static String rootUrl = "/status";
	public static final String metaDataUrl = "/metadata";
	public static final String toLog = "/toLog";
		
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
