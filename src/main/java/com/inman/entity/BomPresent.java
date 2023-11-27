package com.inman.entity;

import javax.persistence.Entity;

@Entity
public class BomPresent extends EntityMaster {

    protected long parentId;
    protected long childId;
    protected double quantityPer;

    protected String parentSummary;
    protected String childSummary;

    protected String childDescription;
    protected double unitCost;
    transient protected double extendedCost;


    public BomPresent() {
        super();
    }

    public BomPresent(BomPresent oldValue ) {
        this.id = oldValue.getId();
        this.parentId = oldValue.getParentId();
        this.childId = oldValue.getChildId();
        this.quantityPer = oldValue.getQuantityPer();
        this.activityState = oldValue.getActivityState();
    }

    public String getParentSummary( ) { return this.parentSummary; }
    public void setParentSummary( String xParentSummary ) { this.parentSummary = xParentSummary; }

    public String getChildSummary( ) { return this.childSummary; }
    public void setChildSummarySummary( String xChildSummary ) { this.childSummary = xChildSummary; }

    public long getChildId() { return childId; }
    public void setChildId( long xChildId ) { childId = xChildId;  }

    public double getQuantityPer( ) { return quantityPer; }
    public void setQuantityPer( double xQuantityPer ) { quantityPer = xQuantityPer; }

    public long getParentId() { return parentId; }
    public void setParentId( long xParentId ) { parentId = xParentId; }

    public String getChildDescription() { return childDescription; }

    public double getUnitCost( ) { return unitCost; };
    public void setUnitCost( double xUnitCost ) { unitCost = xUnitCost; }

    public double getExtendedCost( ) { return quantityPer * unitCost; }

    public void setChildSummary(Object xDatum) {
        this.childSummary = (String) xDatum;
    }

    public void setChildDescription(Object xDatum) {
        this.childDescription = (String) xDatum;
    }
}
