package com.inman.model.request;

import com.inman.entity.BomPresent;

public class BomCrudBatch {
	public static final String updateUrl = "bomArray/update";
	public static final String bomCrud = "bom/crud";
	public static final String BOM_RECURSION_CHECK_URL = "bomRecursionCheck";
	public static final String bomRefreshDepth = "bomRefreshDepth";

	BomPresent[] rows;

	public BomCrudBatch(BomPresent[] xUpdatedRows ) {
		this.rows = xUpdatedRows;
	}

	public BomCrudBatch() {} ;

	public void setRows( BomPresent[] xUpdatedRows ) {
		this.rows = xUpdatedRows;
	}
	public BomPresent[] getRows() { return this.rows; }
}
