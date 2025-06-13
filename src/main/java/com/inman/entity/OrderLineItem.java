package com.inman.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;

@Entity
@Table( name = "OrderLineItem"  )
public class OrderLineItem extends EntityMaster {
	public static String formatter = "%4d %4d %6.2f %6.2f %10s %10s %4s %4s";
	public static String header = String.format( "%-4s %-4s %8s %8s %10s %10s %4s %4s",
			"Ordr", "Item", "Ordered", "Assigned", "Start", "Complete", "Stat", "DbCr");
	int itemId;
	double quantityOrdered = 0.0;
	double quantityAssigned = 0.0;
	String startDate;
	String completeDate;
	OrderState orderState = OrderState.PLANNED;
	DebitCreditIndicator DebitCreditIndicator = com.inman.entity.DebitCreditIndicator.ADDS_TO_BALANCE;

	public OrderLineItem() {
	}


	@Override
	public EntityMaster copy(@NotNull EntityMaster source ) {
		throw new RuntimeException( "Not implemented yet" );
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public double getQuantityOrdered() {
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
		return String.format( formatter, id, itemId, quantityOrdered, quantityAssigned, startDate, completeDate );
	}
}
