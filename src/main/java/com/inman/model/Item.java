package com.inman.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.inman.model.rest.ItemAddRequest;


@Entity
@Table( name = "Item" )
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO )
	private long id;
	
	@Size(min=1,max=10)
	@NotNull
	private String summaryId;
	
	@Size(min=1,max=30)
	@NotNull
	private String description;
	
	@Digits(integer=6,fraction=2)
	@Min(0)
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
