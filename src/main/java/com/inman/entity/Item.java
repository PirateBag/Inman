package com.inman.entity;

import com.inman.model.rest.ItemAddRequest;

import javax.persistence.*;


@Entity
@Table( name = "Item" )
public class  Item extends EntityMaster {

	@Column( unique = true )
	private String summaryId;
	
	private String description;
	
	private double unitCost;

	public static final String SOURCE_PUR = "PUR";
	public static final String SOURCE_MAN = "MAN";
	private String sourcing;
	
	public Item(ItemAddRequest addItemRequest) {
		this.summaryId = addItemRequest.getSummaryId();
		this.description = addItemRequest.getDescription();
		this.unitCost = addItemRequest.getUnitCost();
		this.sourcing = addItemRequest.getSourcing();
	}

	public Item() {
	}

	public long getId() {
		return this.id;
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
	public void setDescription( String description ) {
		this.description = description;
	}
	
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost( double unitCost ) {
		this.unitCost = unitCost;
	}

	public String getSourcing() {
		return sourcing;
	}
	public void setSourcing( String xSourcing ) {
		this.sourcing = xSourcing;
	}


}
