package com.inman.model.rest;

import com.inman.business.Message;
import com.inman.business.QueryParameterException;

public class SearchItemRequest {
	public static final String singleUrl = "item/search/{itemId}";
	public static final String queryUrl = "item/query";
	
	long itemId;
	String summaryId;
	String description;
	
	public SearchItemRequest( String itemId, String summaryId, String description ) throws QueryParameterException {
		int numberOfParameters = 0;
		if ( itemId == null || itemId.length() == 0 ) {
			this.itemId = 0;
		} else {
			this.itemId = Long.valueOf( itemId );
			numberOfParameters++;
		}
		
		if ( summaryId == null || summaryId.trim().length() == 0 ) {
			this.summaryId = null;
		} else {
			this.summaryId = summaryId.trim() + "%";
			numberOfParameters++;
		}
		
		if ( description == null || description.trim().length() == 0 ) {
			this.description = null;
		} else {
			this.description = "%" + description.trim() + "%";
			numberOfParameters++;
		}
		
		if ( numberOfParameters > 1 ) {
			throw new QueryParameterException( Message.ITEM_SEARCH_PARAMETERS );
		}
	}
	
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
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
}
