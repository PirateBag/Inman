package com.inman.model.rest;

import com.inman.business.Message;
import com.inman.business.QueryParameterException;

public class AddItemRequest {
	public static final String addUrl = "item/add";
	
	String summaryId;
	String description;
	double unitCost;
	
	
	public AddItemRequest( String summaryId, String description, double cost ) throws QueryParameterException {
		int numberOfParameters = 0;

		if ( summaryId == null || summaryId.trim().length() == 0 ) {
			this.summaryId = null;
		} else {
			this.summaryId = summaryId.trim();
			numberOfParameters++;
		}
		
		if ( description == null || description.trim().length() == 0 ) {
			this.description = null;
		} else {
			this.description = description.trim();
			numberOfParameters++;
		}
		
		if ( numberOfParameters != 2 ) {
			throw new QueryParameterException( Message.ITEM_SEARCH_PARAMETERS );
		}
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
