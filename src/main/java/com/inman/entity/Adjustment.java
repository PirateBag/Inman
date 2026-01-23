package com.inman.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inman.controller.Utility;
import enums.AdjustmentType;
import enums.CrudAction;
import enums.OrderType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table( name = "Adjustment"  )
public class Adjustment extends EntityMaster {

    public static final String RAW_HEADER_FORMAT =       "%8s    %-6s %-6s  %-4s  %9s  %4s";
    public static final String RAW_LINE_FORMAT =         "%8.2f  %6d  %6d   %-4s  %9s  %4s ";
    public static final String RAW_HEADER = String.format(RAW_HEADER_FORMAT, "Amount", "Item", "Order", "Type", "Date", "AdTp" );


    private double amount;
    private long itemId;

    private long orderId;
    private OrderType orderType;
    private String effectiveDate;  /* YYYY-MMDD */
    private AdjustmentType adjustmentType;

    public Adjustment(double amount, long itemId, long orderId, OrderType orderType, String effectiveDate, AdjustmentType adjustmentType, CrudAction crudAction ) {
        this.amount = amount;
        this.itemId = itemId;
        this.orderId = orderId;
        this.orderType = orderType;
        this.effectiveDate = effectiveDate;
        this.adjustmentType = adjustmentType;
        this.crudAction = crudAction;
        validate();
    }

    public String toString() {
        return String.format(RAW_LINE_FORMAT, amount, itemId, orderId, orderType, effectiveDate, adjustmentType );
    }

    @JsonIgnore
    public void validate() {
        if ( amount == 0.0 ) { throw new IllegalArgumentException( "Adjustment cannot be zero" ); }
        if ( itemId < 0 ) { throw new IllegalArgumentException( "ItemId cannot be negative" ); }

        if ( orderId < 0 && adjustmentType == AdjustmentType.XFER ) { throw new IllegalArgumentException( "OrderId cannot be negative when adjustment type is XFER" ); }

        if ( effectiveDate == null ) { throw new IllegalArgumentException( "EffectiveDate cannot be null" ); }

        Utility.throwIfDateInvalid( effectiveDate );
    }

    public Adjustment() {}

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

    public double getAmount() {
        return amount;
    }

    @Override
    public EntityMaster copy(EntityMaster oldValue) {
        return null;
    }
}
