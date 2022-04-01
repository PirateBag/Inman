package com.inman.model.rest;

import com.inman.business.Message;
import com.inman.business.QueryParameterException;

public class ItemAddRequest {
	public static final String addUrl = "item/add";

	String summaryId;
	String description;
	double unitCost;

	public ItemAddRequest() {
	}
	
	
	public ItemAddRequest( String summaryId, String description, double cost )  {
		this.summaryId = summaryId.trim();
		this.description = description.trim();
		this.unitCost = cost;
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
}
