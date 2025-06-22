package com.inman.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Entity
@Table( name = "OrderLineItem"  )
public class OrderLineItem extends EntityMaster {
	public static String formatter = "%4d %4d %7d %6.2f %6.2f %10s %10s %4s %4s %4s";
	public static String header = String.format( "%-4s %-4s %8s %8s %8s %10s %10s %4s %4s %4s",
			"Id", "Item", "ParentId", "Ordered", "Assigned", "Start", "Complete", "Stat", "DbCr", "Activity");
	long itemId;
	double quantityOrdered = 0.0;
	double quantityAssigned = 0.0;
	String startDate;
	String completeDate;

	public int getParentOliId() {
		return parentOliId;
	}

	public void setParentOliId(int parentOliId) {
		this.parentOliId = parentOliId;
	}

	//	0 when an MO Order header.  Non 0 when a detail of either MO or PO.
	int parentOliId;
	OrderState orderState = OrderState.PLANNED;
	DebitCreditIndicator debitCreditIndicator = com.inman.entity.DebitCreditIndicator.ADDS_TO_BALANCE;

	public OrderLineItem() {
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
		return String.format( formatter, id, itemId, parentOliId, quantityOrdered, quantityAssigned,
				startDate, completeDate, orderState, debitCreditIndicator, activityState );
	}

	public DebitCreditIndicator getDebitCreditIndicator() {
		return debitCreditIndicator;
	}

	public void setDebitCreditIndicator(DebitCreditIndicator debitCreditIndicator) {
		this.debitCreditIndicator = debitCreditIndicator;
	}


}
