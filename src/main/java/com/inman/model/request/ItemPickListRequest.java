package com.inman.model.request;

public class ItemPickListRequest {
    public static final String all = "itemPick/all";

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
