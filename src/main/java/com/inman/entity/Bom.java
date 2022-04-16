package com.inman.entity;

import javax.persistence.*;
@Entity
@Table( name = "Bom"  )
public class Bom extends EntityMaster {
	protected Long parentId;
	protected Long childId;
	protected double quantityPer;

	public Bom() {
	}

	public Bom( Long xParent, Long xChild, Double xQuantityPer ) {
		this.parentId = xParent;
		this.childId = xChild;
		this.quantityPer = xQuantityPer;
	}

	public Long getParentId() {
		return this.parentId;
	}
	public void setParentId( Long xParent ) {
		this.parentId = xParent;
	}

	public Long getChildId() {
		return this.childId;
	}
	public void setChildId( Long xChild ) {
		this.parentId = xChild;
	}

	public double getQuantityPer() {
		return quantityPer;
	}
	public void setQuantityPer(double quantityPer) {
		this.quantityPer = quantityPer;
	}
}