package com.inman.model.request;

public class ItemReportRequest {
	public static final String EXPLOSION_URL = "itemReport/explosion";

	long parentId;

	public ItemReportRequest() { };

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
}
