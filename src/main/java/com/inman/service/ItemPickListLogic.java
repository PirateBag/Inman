package com.inman.service;

import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.entity.Pick;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.ItemPickListResponse;
import com.inman.model.response.ResponseType;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemPickListLogic {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    private BomPresentRepository bomPresentRepository;

    static Logger logger = LoggerFactory.getLogger( "controller: " + ItemPickListLogic.class );

    public ItemPickListResponse getAll( ) {
        var itemPickListResponse = new ItemPickListResponse();
        itemPickListResponse.setResponseType( ResponseType.ADD );

        Item[] items = itemRepository.findAll().toArray(new Item[0]);

        for (Item item : items) {
            Pick pick = new Pick();
            pick.setExternal(  item.getDescription() );
            pick.setId( item.getId() );
            itemPickListResponse.getData().add( pick );

        }

        return itemPickListResponse;
    }

    public ItemPickListResponse getItemsForBom(GenericSingleId genericSingleId) {
        var itemPickListResponseWithAll = new ItemPickListResponse();


        logger.info( "getItemsForBom for item {}", genericSingleId.idToSearchFor() );
        itemPickListResponseWithAll = getAll();

        var allPickItems = itemPickListResponseWithAll.getData();

        showProgress( "Pick Items prior to any filtering.:  ", allPickItems );
        //  Remove the input parameter from the output.
        var filteredPickItems = allPickItems.stream()
               .filter(pick -> pick.getId() != genericSingleId.idToSearchFor() )
                        .toList();

        showProgress( "Pick Items after removing parent:  ", filteredPickItems );

        //  Collect the Ids of the components of the input parameter.
        BomPresent[] boms = bomPresentRepository.findByParentId( genericSingleId.idToSearchFor() );

        showProgress("Component Ids  ", boms );

        var picksAfterRemovingSiblings = filteredPickItems.stream()
                .filter(pick -> isNotInListOfChildren(pick,boms ))
                .toList();

        showProgress( "Pick Items after siblings: ",       picksAfterRemovingSiblings );
        var arrayListAfterRemovingSiblings = convertListToArrayList( picksAfterRemovingSiblings );

        itemPickListResponseWithAll.setData( arrayListAfterRemovingSiblings );
        itemPickListResponseWithAll.setResponseType( ResponseType.ADD );

        return itemPickListResponseWithAll;
    }


    private void showProgress(String description, List<Pick> allPickItems) {
        StringBuilder summaryOfPicks = new StringBuilder( description );
        for ( Pick pick : allPickItems) {
            summaryOfPicks.append("  ").append(pick.getId());
        }
        logger.info(summaryOfPicks.toString());
    }
    private void showProgress(String description, BomPresent[] bomPresents ) {
        StringBuilder summary = new StringBuilder( description );
        for ( BomPresent bomPresent : bomPresents) {
            summary.append("  ").append( bomPresent.getParentId() + ":" + bomPresent.getChildId() );
        }
        logger.info(summary.toString());
    }

    private boolean isNotInListOfChildren(Pick x, BomPresent[] boms) {
        for( BomPresent bom : boms ) {
            if ( x.getId() == bom.getChildId() ) {
                return false;
            }
        }
        return true;
    }

    public ItemPickListResponse getOneItem(GenericSingleId genericSingleId) {
        ItemPickListResponse itemPickListResponse = getAll();

        var allPicks = itemPickListResponse.getData();
        ArrayList<Pick> theOne = new ArrayList<>();
        for (Pick pick : allPicks) {
            if (pick.getId() == genericSingleId.idToSearchFor()) {
                theOne.add( pick );
                itemPickListResponse.setData( theOne);
                break;
            }
        }
        return itemPickListResponse;
    }

    private ArrayList<Pick> convertListToArrayList( List<Pick> picksAsList ) {

        ArrayList<Pick> picksAsArray = new ArrayList<>();
        for ( Pick pick : picksAsList ) {
            picksAsArray.add(  pick );
        }
        return picksAsArray;
    }
}
