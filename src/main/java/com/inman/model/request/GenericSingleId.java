package com.inman.model.request;

/*  **
Generic request that requires a single id, for all types of entity.
 */
public class GenericSingleId {
    public static final String all = "itemPick/all";
    public static final String GET_ONE_ITEM = "itemPick/forOne";
    public static final String ITEMS_FOR_BOM_URL = "itemPick/itemsForBom";

    Long idToSearchFor;

    public GenericSingleId(Long idToSearchFor) {
        this.idToSearchFor = idToSearchFor;
    }

    public GenericSingleId() {}

    public Long getIdToSearchFor() {
        return idToSearchFor;
    }

    public void setIdToSearchFor(Long idToSearchFor) {
        this.idToSearchFor = idToSearchFor;
    }

}
