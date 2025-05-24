package com.inman.model.request;

import com.inman.entity.BomPresent;

public class BomCrudBatch {
	public static final String updateUrl = "bomArray/update";
	public static final String bomCrud = "bom/crud";
	public static final String BOM_RECURSION_CHECK_URL = "bomRecursionCheck";

	BomPresent[] updatedRows;

	public BomCrudBatch(BomPresent[] xUpdatedRows ) {
		this.updatedRows = xUpdatedRows;
	}

	public BomCrudBatch() {} ;

	public void setUpdatedRows( BomPresent[] xUpdatedRows ) {
		this.updatedRows = xUpdatedRows;
	}
	public BomPresent[] getUpdatedRows() { return this.updatedRows; }
}
