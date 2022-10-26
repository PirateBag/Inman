package com.inman.model.request;

public class BomUpdateRequest {
	public static final String updateUrl = "bom/update";
	private static int numberOfExpectedParameters = 2;

	Long id;

	private double quantityPer;


	public BomUpdateRequest(long xId, double xQuantityPer ) {
		this.id = xId;
		this.quantityPer = xQuantityPer;
	}
	
	public long getId() {
		return id;
	}
	public double getQuantityPer() { return quantityPer; }
	public void setQuantityPer( double xQuantityPer ) {
		this.quantityPer = xQuantityPer;
	}
}
