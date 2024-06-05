package com.inman.lists;

import com.inman.entity.Pick;
import com.inman.model.request.ItemPickListRequest;
import com.inman.model.response.ItemPickListResponse;
import org.springframework.web.client.RestTemplate;

import java.util.SortedMap;
import java.util.TreeMap;

public class Items {
    SortedMap<Long, Pick> byId;
    SortedMap<String, Pick> byExternal;
    Pick picks[];

    String findById(long idToFind) {
        Pick pick = byId.get(idToFind);
        return pick.getExternal();
    }

    long findByExternal(String externalToFind) {
        Pick pick = byExternal.get(externalToFind);
        return pick.getId();
    }

    public void refreshData(Pick[] refreshedPicks) {
        byId = new TreeMap<>();
        byExternal = new TreeMap<>();
        picks = refreshedPicks;

        for (Pick pick : refreshedPicks ) {
            byId.put(pick.getId(), pick);
            byExternal.put(pick.getExternal(), pick);
        }
    }

    public void refreshFromServer() {
        String completeUrl = "http://localhost:8080/" + ItemPickListRequest.all;
        RestTemplate restTemplate = new RestTemplate();
        ItemPickListRequest itemPickListRequest = new ItemPickListRequest();
        ItemPickListResponse responsePackage = restTemplate.postForObject(completeUrl, itemPickListRequest, ItemPickListResponse.class);
        assert responsePackage != null;
        refreshData(responsePackage.getData());
    }

    public String [] toStringArray() {
        String [] rValue = new String[ byId.size() ];
        int i = 0;
        for (Long key : byId.keySet()) {
            rValue[ i++ ] = byId.get( key ).getExternal();
        }
        return rValue;
    }

    public int getIndexByid(long parentId) {
        for ( int index = 0; index < byId.size(); index++ ) {
            if ( picks[ index ].getId() == parentId ) {
                return index;
            }
        }
        return -1;
    }
}