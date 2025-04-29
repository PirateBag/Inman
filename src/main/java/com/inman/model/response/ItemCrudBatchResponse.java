package com.inman.model.response;

import com.inman.model.request.ItemCrudSingle;
import com.inman.model.rest.ErrorLine;

import java.util.ArrayList;

public class ItemCrudBatchResponse extends ResponsePackage<ItemCrudSingle> {
    public ItemCrudBatchResponse() {};

    public ItemCrudBatchResponse( ArrayList<ItemCrudSingle> itemSingleResponses, ArrayList<ErrorLine> errors ) {

        setErrors( errors );

        ItemCrudSingle[] itemCrudSingles = new ItemCrudSingle[itemSingleResponses.size()];
        for (int i = 0; i < itemSingleResponses.size(); i++) {
            itemCrudSingles[i] = itemSingleResponses.get(i);
        }

        setData( itemCrudSingles );
        //  this.responseType = xResponsePackage.getResponseType();
    }
};
