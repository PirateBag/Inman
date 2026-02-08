package com.inman.controller;

import org.springframework.http.HttpStatus;

public record  Messages( HttpStatus httpStatus, String text ) {

    public static final String LEAD_TIME_LESS_THAN_1 =
            "Item %d has a lead time of %d but must be > 0";
    public static final Messages LEAD_TIME_LESS_THAN_1_MESSAGE
            = new Messages( HttpStatus.NOT_FOUND, LEAD_TIME_LESS_THAN_1 );

    public static final String ID_OR_SUMMARYID_FOR_CHANGE = "Id (%s) or Summary Id (%s) must indicate an item for Change or delete.";
    public static final Messages ID_OR_SUMMARY_FOR_CHANGE_MESSAGE
            = new Messages( HttpStatus.NOT_FOUND, ID_OR_SUMMARYID_FOR_CHANGE );

    public static Messages ORIGINAL_NOT_FOUND
            = new Messages( HttpStatus.NOT_FOUND, "Unable to retrieve the original %s instance with Id of (%d)"  );

    public static final String ITEM_IDS_MUST_BE_SAME = "Item Ids must be the same for change/delete (%d and %d)";

    public static final String ITEM_REF_NOT_FOUND = "%s item ref (%d) not found";

    public static final String ORDER_REF_NOT_FOUND = "Item (%d) referenced by Order (%d) in %s not found";

    public static final String ITEM_MANUFACTURED = "Item (%s) is manufactured and can't be purchased.";
    public static final String ITEM_PURCHASED = "Item (%s) is purchased and cannot be manufactured.";

    public static final String QUANTITY_ORDERED_GT_0 = "Order quantity (%8.2f) for Item (%s) must be more than zero";

    public static final String QUANTITY_ASSIGNED_NON_0 = "Order assigned (%8.2f) for Item (%s) must be more than zero";

    public static final String NO_DATA_TO_REPORT = "No data (%s) to report";

    public static final String ILLEGAL_STATE = "Illegal state %s";
    public static final Messages ILLEGAL_STATE_MESSAGE = new Messages( HttpStatus.BAD_REQUEST, "Illegal State '%s'" );

    public static final String ADJUST_ORDER_TYPE = "ITEM adjustment can't have an order type.  XFER must have an order type. ";

    public static final String ORDER_AND_ADJUSTMENT_DISAGREE_ITEM = "Order (%d) Item %d and Adjustment Item %d must specify same item.";

    public final static String ITEM_SEARCH_PARAMETERS = "Provide no parameters to see all, or only one parameter to filter on that attribute.";
    public final static String NO_DATA_FOR_PARAMETERS = "These Query Parameters did not match any data";
    public final static String WRONG_NUMBER_OF_PARAMETERS = "Inman expected $1%d parameters but only $2%d parameters were observed";

    public final static String ID_MUST_BE_ZERO_FOR_INSERTS = "Id must be zero for inserts.";

    public static final String UNIT_COST_MUST_BE_ZERO = "Unit cost cannot be set for a MAN item.";

    public static final Messages QUANTITY_PER_DID_NOT_CHANGE = new Messages( HttpStatus.OK, "The quantityPer of %s with an ID of %d did not change." );
    public static final Messages QUANTITY_PER_UPDATED = new Messages( HttpStatus.OK, "BOM (%d) quantityPer was updated from  %f to %f." );

    public static final Messages RERETRIEVE = new Messages( HttpStatus.NOT_FOUND, "Unable to re-retrive %s with an Id of (%d)"  );

    public static final Messages DATA_INTEGRITY = new Messages( HttpStatus.BAD_REQUEST, "Validation on insert BOM parent %d: child %d because %s " );

    public static final Messages ROW_UPDATED = new Messages( HttpStatus.OK, "%s, Id %d has been %sd." );

    public static final Messages BOM_NOT_FOUND = new Messages( HttpStatus.BAD_REQUEST, "Unable to find BOM Id %d." );

}
