package com.inman.entity;

import com.inman.controller.Utility;
import com.inman.model.rest.ItemAddRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;

@Entity
@Table( name = "Item" )
public class  Item extends EntityMaster {
	public static String toStringFormat = "%04d: %s,%s %8.2f %s %3d %3d";
	@Column( unique = true )
	private String summaryId;
	
	private String description;
	
	private double unitCost;

	public static final String SOURCE_PUR = "PUR";
	public static final String SOURCE_MAN = "MAN";
	private String sourcing;

	private int maxDepth = 0;

	private int leadTime = 0;
	
	public Item(ItemAddRequest addItemRequest) {
		this.summaryId = addItemRequest.getSummaryId();
		this.description = addItemRequest.getDescription();
		this.unitCost = addItemRequest.getUnitCost();
		this.sourcing = addItemRequest.getSourcing();
	}


	public Item(String summaryId, String description, double unitCost, String sourcing, int leadTime, int maxDepth ) {
		this.summaryId = summaryId;
		this.description = description;
		this.unitCost = unitCost;
		this.sourcing = sourcing;
		this.leadTime = leadTime;
		this.maxDepth = maxDepth;
	}

	public Item() {
	}



	@Override

	public EntityMaster copy( @NotNull EntityMaster entityMaster ) {
		var rValue = new Item();
		Item item = (Item) entityMaster;
		rValue.id = item.getId();
		rValue.activityState = item.getActivityState();
		rValue.summaryId = item.getSummaryId();
		rValue.description = item.getDescription();
		rValue.unitCost = item.getUnitCost();
		rValue.sourcing = item.getSourcing();
		rValue.maxDepth = item.maxDepth;
		rValue.leadTime = item.leadTime;
		return rValue;
	}

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

	public String getSourcing( ) {
		return Utility.normalize( sourcing );
	}
	public void setSourcing( String xSourcing ) {
		this.sourcing = xSourcing;
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
		return toStringFormat.formatted( id, summaryId, description, unitCost, sourcing, maxDepth, leadTime );
	}
}
