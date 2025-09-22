package com.inman.entity;

import enums.SourcingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.max;


@Entity
@Table( name = "Item" )
public class  Item extends EntityMaster {
    // Use private instead of public to hide the field within class
    private static final String toStringFormat = "%04d: %s,%s %8.2f %s %3d %3d %8.2f %8.2f";

    @Column(unique = true)
    private String summaryId;
    private String description;
    private double unitCost;
    private SourcingType sourcing;
    private int maxDepth = 0;
    private int leadTime = 0;
    private double quantityOnHand;
    private double minimumOrderQuantity;

	public Item(String summaryId, String description, double unitCost, SourcingType sourcing, int leadTime, int maxDepth,
				double quantityOnHand, double minimumOrderQuantity ) {
		this.summaryId = summaryId;
		this.description = description;
		this.unitCost = unitCost;
		this.sourcing = sourcing;
		this.leadTime = leadTime;
		this.maxDepth = maxDepth;
		this.quantityOnHand = quantityOnHand;
		this.minimumOrderQuantity = minimumOrderQuantity;
	}

	public Item() {
	}

	@Override

	public EntityMaster copy( @NotNull EntityMaster entityMaster ) {
		var rValue = new Item();
		Item item = (Item) entityMaster;
		rValue.id = item.getId();
		rValue.crudAction = item.getCrudAction();
		rValue.summaryId = item.getSummaryId();
		rValue.description = item.getDescription();
		rValue.unitCost = item.getUnitCost();
		rValue.sourcing = item.getSourcing();
		rValue.maxDepth = item.maxDepth;
		rValue.leadTime = item.leadTime;
		rValue.quantityOnHand = item.quantityOnHand;
		rValue.minimumOrderQuantity = item.minimumOrderQuantity;
		return rValue;
	}

	@NotNull
	public String getSummaryId() {
		return summaryId == null ? "" : summaryId;
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


	public int getMaxDepth() { return maxDepth; }

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(int leadTime) {
		this.leadTime = leadTime;
	}

	public String toString(){
		return toStringFormat.formatted( id, summaryId, description, unitCost, sourcing, maxDepth, leadTime, quantityOnHand, minimumOrderQuantity );
	}
	public double getQuantityOnHand() { return quantityOnHand; }
	public void setQuantityOnHand( double quantityOnHand ) { this.quantityOnHand = quantityOnHand; }


	public SourcingType getSourcing() {
		return sourcing;
	}

	public void setSourcing(SourcingType sourcing) {
		this.sourcing = sourcing;
	}

	public double getMinimumOrderQuantity() { return minimumOrderQuantity; }
	public void setMinimumOrderQuantity(double minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }

	public double applyOrderQuantityRules(double orderQuantity) {
		return max( orderQuantity, this.minimumOrderQuantity );
	}
}
