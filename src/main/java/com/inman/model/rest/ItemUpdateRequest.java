package com.inman.model.rest;

import com.inman.business.Message;
import com.inman.business.QueryParameterException;

public class ItemUpdateRequest {
	public static final String updateUrl = "item/update";
	private static int numberOfExpectedParameters = 2;
	
	long id;
	String summaryId;
	String description;
	double unitCost;
	
	public ItemUpdateRequest() {
		
	}
	
	
	public ItemUpdateRequest( long id, String summaryId, String description, double cost ) throws QueryParameterException {
		int numberOfParameters = 0;
		
		this.id = id;

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
