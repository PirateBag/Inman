package com.inman.model.rest;

public class ItemAddRequest {
	public static final String addUrl = "item/add";

	String summaryId;
	String description;
	double unitCost;
	String sourcing;

	public ItemAddRequest() {
	}
	
	
	public ItemAddRequest( String summaryId, String description, double cost, String xSourcing )  {
		this.summaryId = summaryId.trim();
		this.description = description.trim();
		this.unitCost = cost;
		this.sourcing = xSourcing;
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

    public String getSourcing() { return this.sourcing; }
    public void setSourcing( String xSourcing ) { this.sourcing = xSourcing; }
}
