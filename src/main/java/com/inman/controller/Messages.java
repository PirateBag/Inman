package com.inman.controller;

public class Messages {
    public static final String LEAD_TIME_LESS_THAN_1 =
            "Item %d has a lead time of %d but must be > 0";

    public static final String ID_OR_SUMMARYID_FOR_CHANGE = "Id (%s) or Summary Id (%s) must indicate an item for Change or delete.";

    public static final String SUMMARY_ID_NOT_FOUND = "Item with SummaryId (%s) not found";

    public static final String ITEM_IDS_MUST_BE_SAME = "Item Ids must be the same for change/delete (%d and %d)";
}
