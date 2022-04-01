package com.inman.model.rest;

public class ItemUpdateRequest {
	public static final String updateUrl = "item/update";
	private static int numberOfExpectedParameters = 2;
	
	long id;
	String summaryId;
	String description;
	double unitCost;
	
	public ItemUpdateRequest() {
		
	}
	
	
	public ItemUpdateRequest( long id, String summaryId, String description, double cost ) {
		this.id = id;
		this.summaryId = summaryId.trim();
		this.description = description.trim();
		this.unitCost = cost;
	}
	
	public long getId() {
		return id;
	}
	public String getSummaryId() {
		return summaryId;
	}
	public void setSummaryId(String summaryId) {
		this.summaryId = summaryId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost( double cost) {
		this.unitCost = cost;
	}
	public void setId(long id ) {
		this.id = id;
	}
}
