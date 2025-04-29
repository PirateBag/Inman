package com.inman.business;

import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.entity.Pick;
import com.inman.model.request.ItemPickListRequest;
import com.inman.model.response.ItemPickListResponse;
import com.inman.model.response.ResponseType;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
        Pick[] picks = new Pick[ items.length ];
        int i = 0;
        for ( Item item : items ) {
            picks[ i++ ] = new Pick( item.getId(), Pick.formatExternalFromSummaryDescription( item.getSummaryId(), item.getDescription() ) );
        }

        itemPickListResponse.setData(picks);
        return itemPickListResponse;
    }

    public ItemPickListResponse getItemsForBom(ItemPickListRequest itemPickListRequest ) {
        var itemPickListResponseWithAll = new ItemPickListResponse();

        assert itemPickListRequest.getIdToSearchFor()  != null;
        assert itemPickListRequest.getIdToSearchFor() > 0L;

        logger.info( "getItemsForBom for item {}", itemPickListRequest.getIdToSearchFor() );
        itemPickListResponseWithAll = getAll();

        var pickItems = itemPickListResponseWithAll.getData();

        StringBuilder summaryOfPicks = new StringBuilder("Pick Items prior to any filtering.:  ");
        for ( Pick pick : pickItems ) {
            summaryOfPicks.append("  ").append(pick.getId());
        }
        logger.info(summaryOfPicks.toString());

        //  Remove the input parameter from the output.
        pickItems = (Pick[])
                Arrays.stream(pickItems)
                .filter(pick -> pick.getId() != itemPickListRequest.getIdToSearchFor() )
                        .toArray( Pick[]::new );

        summaryOfPicks = new StringBuilder("Pick Items after removing parent:  ");
        for ( Pick pick : pickItems ) {
            summaryOfPicks.append("  ").append(pick.getId());
        }
        logger.info(summaryOfPicks.toString());

        //  Collect the Ids of the components of the input parameter.
        BomPresent[] boms = bomPresentRepository.findByParentId( itemPickListRequest.getIdToSearchFor() );

        summaryOfPicks = new StringBuilder("Component Ids  ");
        for ( BomPresent bom  : boms  ) {
            summaryOfPicks.append("  ").append(bom.getChildId());
        }
        logger.info(summaryOfPicks.toString());

        pickItems = (Pick[])
                Arrays.stream(pickItems)
                        .filter( x ->isNotInListOfChildren( x, boms ) )
                        .toArray( Pick[]::new );

        summaryOfPicks = new StringBuilder("Pick Items after removing parent: ");
        for ( Pick pick : pickItems ) {
            summaryOfPicks.append("  ").append(pick.getId());
        }
        logger.info(summaryOfPicks.toString());


        itemPickListResponseWithAll.setData( pickItems );

        summaryOfPicks = new StringBuilder("Pick Items after removing siblings: ");
        for ( Pick pick : pickItems ) {
            summaryOfPicks.append("  ").append(pick.getId());
        }
        logger.info(summaryOfPicks.toString());


        return itemPickListResponseWithAll;
    }

    private boolean isNotInListOfChildren(Pick x, BomPresent[] boms) {
        for( BomPresent bom : boms ) {
            if ( x.getId() == bom.getChildId() ) {
                return false;
            }
        }
        return true;
    }

    public ItemPickListResponse getOneItem(ItemPickListRequest itemPickListRequest) {
        assert itemPickListRequest.getIdToSearchFor() != null;
        assert itemPickListRequest.getIdToSearchFor() > 0L;
        ItemPickListResponse itemPickListResponse = getAll();

        Pick[] allPicks = itemPickListResponse.getData();
        Pick[] theOne = new Pick[1];
        for (Pick pick : allPicks) {
            if (pick.getId() == itemPickListRequest.getIdToSearchFor()) {
                theOne[0] = pick;
                itemPickListResponse.setData(theOne);
                break;
            }
        }
        return itemPickListResponse;
    }
}
