package com.inman.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO )
	private long id;
	
	private String summaryId;
	private String description;
	private double unitCost;
	
	public String getSummaryId() {
		return summaryId;
	}
	public void setSummaryId(String summaryId) {
		this.summaryId = summaryId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription( String description ) {
		this.description = description;
	}
	
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost( double unitCost ) {
		
	}

}