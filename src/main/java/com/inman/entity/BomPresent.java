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

    public BomPresent() {
        super();
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

}
