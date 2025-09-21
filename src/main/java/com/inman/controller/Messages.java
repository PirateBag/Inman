package com.inman.controller;

public class Messages {
    public static final String LEAD_TIME_LESS_THAN_1 =
            "Item %d has a lead time of %d but must be > 0";

    public static final String ID_OR_SUMMARYID_FOR_CHANGE = "Id (%s) or Summary Id (%s) must indicate an item for Change or delete.";

    public static final String SUMMARY_ID_NOT_FOUND = "Item with SummaryId (%s) not found";

    public static final String ITEM_IDS_MUST_BE_SAME = "Item Ids must be the same for change/delete (%d and %d)";

    public static final String ITEM_REF_NOT_FOUND = "%s item ref (%d) not found";

    public static final String ORDER_REF_NOT_FOUND = "%s item ref (%d) not found";

    public static final String ITEM_MANUFACTURED = "Item (%s) is manufactured and can't be purchased.";
    public static final String ITEM_PURCHASED = "Item (%s) is purchased and cannot be manufactured.";

    public static final String QUANTITY_ORDERED_GT_0 = "Order quantity (%8.2f) for Item (%s) must be more than zero";

    public static final String QUANTITY_ASSIGNED_NON_0 = "Order assigned (%8.2f) for Item (%s) must be more than zero";

    public static final String NO_DATA_TO_REPORT = "No data (%s) to report";

    public static final String ILLEGAL_STATE = "Illegal state %s";

    public static final String ADJUST_ORDER_TYPE = "ITEM adjustment can't have an order type.  XFER must have an order type. ";

    public static final String ORDER_AND_ADJUSTMENT_DISAGREE_ITEM = "Order (%d) Item %d and Adjustment Item %d must specify same item.";

    public final static String ITEM_SEARCH_PARAMETERS = "Provide no parameters to see all, or only one parameter to filter on that attribute.";
    public final static String NO_DATA_FOR_PARAMETERS = "These Query Parameters did not match any data";
    public final static String WRONG_NUMBER_OF_PARAMETERS = "Inman expected $1%d parameters but only $2%d parameters were observed";

}
