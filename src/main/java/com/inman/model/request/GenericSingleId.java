package com.inman.model.request;

/*  **
Generic request that requires a single id, for all types of entity.
 */
public record  GenericSingleId ( long idToSearchFor, String options )
{
    public static final String all = "itemPick/all";
    public static final String GET_ONE_ITEM = "itemPick/forOne";
    public static final String ITEMS_FOR_BOM_URL = "itemPick/itemsForBom";

    public static final String OPTION_CONSOLIDATE_ORDERS = "CONSOLIDATE_ORDERS";
    public static final String OPTION_NONE = "";
}
