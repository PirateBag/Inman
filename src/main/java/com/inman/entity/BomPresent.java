package com.inman.entity;

public class BomPresent extends Bom {
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
