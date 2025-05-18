package com.inman.lists;

import com.inman.entity.Pick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.SortedMap;
import java.util.TreeMap;


public class Items {
    static Logger logger = LoggerFactory.getLogger(Items.class );
    SortedMap<Long, Pick> byId;
    SortedMap<String, Pick> byExternal;
    Pick[] picks;
    String itemPickListUrlSuffix;
    RestTemplate restTemplate = new RestTemplate();

    public Items( String itemPickListUrlSuffix ) {
        this.itemPickListUrlSuffix = itemPickListUrlSuffix;
    }

    String findById(long idToFind) {
        Pick pick = byId.get(idToFind);
        return pick.getExternal();
    }

    long findByExternal(String externalToFind) {
        Pick pick = byExternal.get(externalToFind);
        return pick.getId();
    }

    public long findIdByArrayIndex(int indexToFind) {
        assert indexToFind >= 0 && indexToFind < picks.length;
        logger.info("indexToFind: {} out of {}", indexToFind, picks.length );
        logger.info("object is: {}", picks[ indexToFind ]);
        long l = picks[indexToFind].getId();
        return l;
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
/*
    public void refreshFromServer(Optional<Long> idParameter ) {
        String completeUrl = "http://localhost:8080/" + itemPickListUrlSuffix;
        ItemPickListRequest itemPickListRequest;
        itemPickListRequest = idParameter.map(ItemPickListRequest::new).orElseGet(ItemPickListRequest::new);
        ItemPickListResponse responsePackage = restTemplate.postForObject(completeUrl, itemPickListRequest, ItemPickListResponse.class);
        assert responsePackage != null;
        refreshData(responsePackage.getData());
    }
*/

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

    public boolean isEmpty() {
        return picks.length == 0;
    }
}