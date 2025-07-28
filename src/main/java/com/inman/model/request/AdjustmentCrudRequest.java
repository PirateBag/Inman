package com.inman.model.request;

import com.inman.entity.Adjustment;

public class AdjustmentCrudRequest  {

	Adjustment[] rows;

	public AdjustmentCrudRequest() {} ;

	public void setRows( Adjustment[] rows ) {
		this.rows = rows;
	}
	public Adjustment[] getRows() { return this.rows; }
}
