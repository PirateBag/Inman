package com.inman.model.response;

import com.inman.entity.Item;

public class ItemResponse extends ResponsePackage<Item> {
    public ItemResponse() {};

    public ItemResponse( ResponseType xResponseType ) {
        responseType = xResponseType;
    }
};
