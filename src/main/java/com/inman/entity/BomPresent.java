package com.inman.entity;

import jakarta.persistence.Entity;
import org.jetbrains.annotations.NotNull;

@Entity
public class BomPresent extends EntityMaster {

    protected long parentId;
    protected long childId;
    protected double quantityPer;

    protected String parentDescription;
    protected String childDescription;
    protected double unitCost;

    public BomPresent() {
        super();
    }


    @Override
    public EntityMaster copy(@NotNull EntityMaster oldValue ) {
        BomPresent rValue = new BomPresent();
        BomPresent bomPresent = (BomPresent) oldValue;
        rValue.id = bomPresent.getId();
        rValue.crudAction = oldValue.getCrudAction();
        rValue.parentId = bomPresent.getParentId();
        rValue.parentDescription = bomPresent.getParentDescription();
        rValue.childId = bomPresent.getChildId();
        rValue.quantityPer = bomPresent.getQuantityPer();
        rValue.unitCost = bomPresent.getUnitCost();
        return rValue;
    }


    public long getChildId() { return childId; }
    public void setChildId( long xChildId ) { childId = xChildId;  }

    public double getQuantityPer( ) { return quantityPer; }
    public void setQuantityPer( double xQuantityPer ) { quantityPer = xQuantityPer; }

    public long getParentId() { return parentId; }
    public void setParentId( long xParentId ) { parentId = xParentId; }

    public String getChildDescription() { return childDescription; }

    public double getUnitCost( ) { return unitCost; }
    public void setUnitCost( double xUnitCost ) { unitCost = xUnitCost; }

    public double getExtendedCost( ) { return quantityPer * unitCost; }

    public void setChildDescription(Object xDatum) {
        this.childDescription = (String) xDatum;
    }

    public String getParentDescription() { return this.parentDescription; }
    public void setParentDescription( String parentDescription ) { this.parentDescription = parentDescription; }

    public String toString() {
        return String.format( "%-30s %-30s %6.3f %8.3f", parentDescription, childDescription, quantityPer, unitCost );
    }
}
