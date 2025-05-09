package com.inman.model.request;

import com.inman.entity.BomPresent;

public class BomUpdate {
	public static final String updateUrl = "bomArray/update";
	public static final String BOM_RECURSION_CHECK_URL = "bomRecursionCheck";

	BomPresent[] updatedRows;

	public BomUpdate( BomPresent[] xUpdatedRows ) {
		this.updatedRows = xUpdatedRows;
	}

	public BomUpdate() {} ;

	public void setUpdatedRows( BomPresent[] xUpdatedRows ) {
		this.updatedRows = xUpdatedRows;
	}
	public BomPresent[] getUpdatedRows() { return this.updatedRows; }
}
