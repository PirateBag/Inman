package com.inman.model.rest;

import com.inman.business.Message;
import com.inman.business.QueryParameterException;

public class ItemAddRequest {
	public static final String addUrl = "item/add";
	private static int numberOfExpectedParameters = 2;
	
	String summaryId;
	String description;
	double unitCost;
	
	public ItemAddRequest() {
		
	}
	
	
	public ItemAddRequest( String summaryId, String description, double cost ) throws QueryParameterException {
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
		
		this.unitCost = cost;

		
		if ( numberOfParameters != numberOfExpectedParameters ) {
			throw new QueryParameterException( String.format( Message.WRONG_NUMBER_OF_PARAMETERS, 2, numberOfExpectedParameters ) );
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
