package com.inman.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inman.controller.Utility;
import enums.AdjustmentType;
import enums.OrderType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table( name = "Adjustment"  )
public class Adjustment extends EntityMaster {

    private double adjustment;
    private long itemId;

    private long orderId;
    private OrderType orderType;
    private String effectiveDate;  /* YYYY-MMDD */
    private AdjustmentType adjustmentType;

    public Adjustment(double adjustment, long itemId, long orderId, OrderType orderType, String effectiveDate, AdjustmentType adjustmentType) {
        this.adjustment = adjustment;
        this.itemId = itemId;
        this.orderId = orderId;
        this.orderType = orderType;
        this.effectiveDate = effectiveDate;
        this.adjustmentType = adjustmentType;
        validate();
    }

    public Adjustment() {

    }

    @JsonIgnore
    public void validate() {
        if ( adjustment == 0.0 ) { throw new IllegalArgumentException( "Adjustment cannot be zero" ); }
        if ( itemId < 0 ) { throw new IllegalArgumentException( "ItemId cannot be negative" ); }

        if ( orderId < 0 && adjustmentType == AdjustmentType.XFER ) { throw new IllegalArgumentException( "OrderId cannot be negative when adjustment type is XFER" ); }

        if ( effectiveDate == null ) { throw new IllegalArgumentException( "EffectiveDate cannot be null" ); }

        Utility.isDateFormatValid( effectiveDate );
    }


    public AdjustmentType getAdjustmentType() {
        return adjustmentType;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getItemId() {
        return itemId;
    }

    public double getAdjustment() {
        return adjustment;
    }

    public void validation() {

    }
    @Override
    public EntityMaster copy(EntityMaster oldValue) {
        return null;
    }
}
