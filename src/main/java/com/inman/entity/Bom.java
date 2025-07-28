package com.inman.entity;

import enums.CrudAction;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;
@Entity
@Table( name = "Bom"  )
public class Bom extends EntityMaster {
	public static String formatter = "%4d %4d %4d %6.2f %-10s";
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

	public Bom( long id, long parentId, long childId, double quanittyPer, CrudAction curdAction) {
		this.id = id;
		this.parentId = parentId;
		this.childId = childId;
		this.quantityPer = quanittyPer;
		this.crudAction = curdAction;
	}

	@Override
	public EntityMaster copy(@NotNull EntityMaster source ) {
		Bom rValue = new Bom();
		Bom oldValue = (Bom) source;
		rValue.id = oldValue.getId();
		rValue.crudAction = oldValue.crudAction;
		rValue.parentId = oldValue.getParentId();
		rValue.childId = oldValue.getChildId();
		rValue.quantityPer = oldValue.getQuantityPer();
		return rValue;
	}

	public String toString() {
		return String.format(formatter, id, parentId, childId, quantityPer, crudAction);
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
