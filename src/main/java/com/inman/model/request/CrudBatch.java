package com.inman.model.request;

import com.inman.entity.BomPresent;

public class CrudBatch<T>  {

	T[] rows;

	public CrudBatch(T[] xUpdatedRows ) {
		this.rows = xUpdatedRows;
	}

	public CrudBatch() {} ;

	public void setUpdatedRows( T[] xUpdatedRows ) {
		this.rows = xUpdatedRows;
	}
	public T[] getUpdatedRows() { return this.rows; }
}
