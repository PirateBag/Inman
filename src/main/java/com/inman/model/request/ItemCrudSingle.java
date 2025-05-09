package com.inman.model.request;

import com.inman.entity.ActivityState;
import com.inman.entity.EntityMaster;
import com.inman.entity.Item;
import com.inman.repository.ItemRepository;

import java.util.Objects;

public class ItemCrudSingle extends EntityMaster {
    private String summaryId;
    private String description;
    private double unitCost;
    private String sourcing;

    public ItemCrudSingle(
            String summaryId,
            String description,
            double unitCost,
            String sourcing,
            ActivityState activityState) {
        this.summaryId = summaryId;
        this.description = description;
        this.unitCost = unitCost;
        this.sourcing = sourcing;
        this.activityState = activityState;
    }

    public String getSummaryId() {
        return summaryId;
    }

    public String getDescription() {
        return description;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public String getSourcing() {
        return sourcing;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ItemCrudSingle) obj;
        return Objects.equals(this.summaryId, that.summaryId) &&
                Objects.equals(this.description, that.description) &&
                Double.doubleToLongBits(this.unitCost) == Double.doubleToLongBits(that.unitCost) &&
                Objects.equals(this.sourcing, that.sourcing) &&
                Objects.equals(this.activityState, that.activityState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaryId, description, unitCost, sourcing, activityState);
    }

    @Override
    public String toString() {
        return "ItemCrudSingle[" +
                "summaryId=" + summaryId + ", " +
                "description=" + description + ", " +
                "unitCost=" + unitCost + ", " +
                "sourcing=" + sourcing + ", " +
                "activityState=" + activityState + ']';
    }

    public Item generateItem( long xId) {
        var rValue = new Item();
        rValue.setId( xId );
        rValue.setSummaryId(this.summaryId);
        rValue.setDescription(this.description);
        rValue.setUnitCost(this.unitCost);
        rValue.setSourcing(this.sourcing);
        rValue.setActivityState( ActivityState.NONE);
        return rValue;
    }

    public Item generateItem() {
        var rValue = new Item();
        rValue.setId( this.id );
        rValue.setSummaryId(this.summaryId);
        rValue.setDescription(this.description);
        rValue.setUnitCost(this.unitCost);
        rValue.setSourcing(this.sourcing);
        rValue.setActivityState( ActivityState.NONE);
        return rValue;
    }


    @Override
    public EntityMaster copy(EntityMaster oldValue) {
        return null;
    }
}

