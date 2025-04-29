package com.inman.model.request;

public class ItemPickListRequest {
    public static final String all = "itemPick/all";
    public static final String GET_ONE_ITEM = "itemPick/forOne";
    public static final String ITEMS_FOR_BOM_URL = "itemPick/itemsForBom";

    Long idToSearchFor;

    public ItemPickListRequest(Long idToSearchFor) {
        this.idToSearchFor = idToSearchFor;
    }

    public ItemPickListRequest() {}

    public Long getIdToSearchFor() {
        return idToSearchFor;
    }

    public void setIdToSearchFor(Long idToSearchFor) {
        this.idToSearchFor = idToSearchFor;
    }

}
