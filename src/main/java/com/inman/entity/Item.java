package com.inman.entity;

import javax.persistence.*;

import com.inman.model.rest.ItemAddRequest;


@Entity
@Table( name = "Item" )
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO )
	private long id;

	@Column( unique = true )
	private String summaryId;
	
	private String description;
	
	private double unitCost;
	
	public Item(ItemAddRequest addItemRequest) {
		this.summaryId = addItemRequest.getSummaryId();
		this.description = addItemRequest.getDescription();
		this.unitCost = addItemRequest.getUnitCost();
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

}
