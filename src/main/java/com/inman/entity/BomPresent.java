package com.inman.entity;

import javax.persistence.Entity;

@Entity
public class BomPresent extends EntityMaster {

    protected long parentId;
    protected long childId;
    protected double quantityPer;

    protected String parentSummary;
    protected String childSummary;

    public BomPresent() {
        super();
    }

    public String getParentSummary( ) { return this.parentSummary; }
    public void setParentSummary( String xParentSummary ) { this.parentSummary = xParentSummary; }

    public String getChildSummary( ) { return this.childSummary; }
    public void setChildSummarySummary( String xChildSummary ) { this.childSummary = xChildSummary; }

}
