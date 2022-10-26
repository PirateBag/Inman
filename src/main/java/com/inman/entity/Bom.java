package com.inman.entity;

import javax.persistence.*;
@Entity
@Table( name = "Bom"  )
public class Bom extends EntityMaster {

	protected long parentId;
	protected long childId;
	protected double quantityPer;

	public Bom() {
	}

	public Bom(Long xParent, Long xChild, Double xQuantityPer ) {
		this.parentId = xParent;
		this.childId = xChild;
		this.quantityPer = xQuantityPer;
	}

	public Bom(Long xParent, Long xChild, Double xQuantityPer, String xParentSummary ) {
		this.parentId = xParent;
		this.childId = xChild;
		this.quantityPer = xQuantityPer;
	}


	public long getParentId() {
		return this.parentId;
	}
	public void setParentId( Long xParent ) {
		this.parentId = xParent;
	}

	public long getChildId() {
		return this.childId;
	}
	public void setChildId( Long xChild ) {
		this.childId = xChild;
	}

	public double getQuantityPer() {
		return quantityPer;
	}
	public void setQuantityPer(double quantityPer) {
		this.quantityPer = quantityPer;
	}


}
