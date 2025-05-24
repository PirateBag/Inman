package com.inman.model.request;

public class ItemReportRequest {
	public static final String SHOW_ALL_ITEMS_URL = "itemReport/showAllItems";
	public static final String EXPLOSION_URL = "itemReport/explosion";
    public static final String BOM_RECURSION_CHECK_URL = "itemReport/bomRecursionCheck";
	public static final String WHERE_USED_REPORT_URL = "itemReport/whereUsedReport";

    long parentId;
	long childId;

	public ItemReportRequest( Long itemId ) {
		parentId = itemId;
	}

	public ItemReportRequest() { };

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getChildId() {
		return childId;
	}

	public void setChildId(long childId) {
		this.childId = childId;
	}

}
