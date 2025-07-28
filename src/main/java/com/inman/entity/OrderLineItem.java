package com.inman.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inman.service.ReflectionHelpers;
import enums.OrderState;
import enums.OrderType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;


@Entity
@Table( name = "OrderLineItem"  )
public class OrderLineItem extends EntityMaster {
	public static String formatter = "%4d %4d %8d %8.2f %8.2f %10s %10s %4s %6s %4s";
	public static String header = String.format( "%4s %4s %7s %8s %8s %10s %10s %4s %6s %4s",
			"Id", "Item", "ParentId", "Ordered", "Assigned", "Start", "Complete", "Stat", "Type", "Acti");
	public static Map<String, Field> fieldNames;

	long itemId;
	double quantityOrdered = 0.0;
	double quantityAssigned = 0.0;
	String startDate;
	String completeDate;

	//	0 when an MO Order header.  Non 0 when a detail of either MO or PO.
	long parentOliId;

	OrderState orderState = OrderState.PLANNED;

	OrderType orderType;

	public OrderLineItem() {
	}

	public OrderLineItem( OrderLineItem orderLineItem ) {
		this.id = orderLineItem.getId();
		this.crudAction = orderLineItem.getCrudAction();
		this.itemId = orderLineItem.getItemId();
		this.quantityOrdered = orderLineItem.getQuantityOrdered();
		this.quantityAssigned = orderLineItem.getQuantityAssigned();
		this.startDate = orderLineItem.getStartDate();
		this.completeDate = orderLineItem.getCompleteDate();
		this.parentOliId = orderLineItem.getParentOliId();
		this.orderState = orderLineItem.getOrderState();
		this.orderType = orderLineItem.getOrderType();
	}


	@Override
	public EntityMaster copy(@NotNull EntityMaster source ) {
		throw new RuntimeException( "Not implemented yet" );
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public double getQuantityOrdered() {
		return quantityOrdered;
	}

	@JsonIgnore
	public double getEffectiveQuantityOrdered() {
		if ( orderType == OrderType.MODET )
			return -quantityOrdered;

		return quantityOrdered;
	}

	public void setQuantityOrdered(double quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public double getQuantityAssigned() {
		return quantityAssigned;
	}

	public void setQuantityAssigned(double quantityAssigned) {
		this.quantityAssigned = quantityAssigned;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	public String toString() {
		return String.format( formatter,id, itemId, parentOliId, quantityOrdered, quantityAssigned,
				startDate, completeDate, orderState, orderType, crudAction );
	}

	public String toStringWithSignedQuantity() {
		return String.format( formatter,id, itemId, parentOliId, getEffectiveQuantityOrdered(), quantityAssigned,
				startDate, completeDate, orderState, orderType, crudAction);
	}

	public long getParentOliId() {
		return parentOliId;
	}

	public void setParentOliId( long parentOliId) {
		this.parentOliId = parentOliId;
	}

	public OrderState getOrderState() {
		return orderState;
	}

	public void setOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public static Map<String, Field> getFieldNames() {
		if ( null == fieldNames ) {
			fieldNames = ReflectionHelpers.setOfFields(OrderLineItem.class);
		}
		return fieldNames;
	}
}
