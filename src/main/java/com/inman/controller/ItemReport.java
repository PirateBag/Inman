package com.inman.controller;

import com.inman.business.BomNavigation;
import com.inman.business.Common;
import com.inman.business.ItemReportService;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.model.request.ItemReportRequest;
import com.inman.model.response.ItemExplosionResponse;
import com.inman.model.response.ResponseType;
import com.inman.model.rest.ErrorLine;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;


@Configuration
@RestController
public class ItemReport {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BomPresentRepository bomPresentRepository;

    static Logger logger = LoggerFactory.getLogger("controller: " + ItemReport.class);
    @Autowired
    private BomNavigation bomNavigation;

    ItemReportService itemReportService;


    public ItemReport(
            @Autowired
            ItemReportService itemReportService
    ) {
        this.itemReportService = itemReportService;
    }

    @CrossOrigin
    @RequestMapping(value = ItemReportRequest.EXPLOSION_URL, method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json")
    private ItemExplosionResponse handleItemExplosionRequest(@RequestBody ItemReportRequest itemReportRequest) {
        List<String> cummulativeResponse = generateItemExplosionReport(itemReportRequest.getParentId(), null, 0);

        ItemExplosionResponse rValue = new ItemExplosionResponse(cummulativeResponse.toArray(new String[0]));
        rValue.setResponseType(ResponseType.QUERY);
        return rValue;
    }

    private List<String> generateItemExplosionReport(long itemId,
                                                     List<String> cumulativeResponse,
                                                     long parentLevel) {
        String lineContents = "%-10s  %-30s  %10.2f  %3s  %10d";
        String lineHeader = "%-10s  %-30s  %10s  %3s  %10sd";

        if (cumulativeResponse == null) {
            cumulativeResponse = new LinkedList<>();
            var message = String.format(lineHeader,
                    "Summary", "Description", "Unit Cost", "Sourcing", "Depth");
            cumulativeResponse.add(message);
            logger.info(message);
        }

        Item item = itemRepository.findById(itemId);

        if (item == null) {
            var message = "item not found:" + itemId;
            cumulativeResponse.add(message);
            logger.error(message);
            return null;
        }

        var message = Common.spacesForLevel(parentLevel) + String.format(lineContents,
                item.getSummaryId(),
                item.getDescription(),
                item.getUnitCost(),
                item.getSourcing(),
                item.getMaxDepth());

        logger.info(message);
        cumulativeResponse.add(message);

        BomPresent[] components = bomPresentRepository.findByParentId(itemId);

        for (BomPresent bom : components) {
            generateItemExplosionReport(bom.getChildId(), cumulativeResponse, parentLevel + 1);
        }


        return cumulativeResponse;
    }

    @CrossOrigin
    @RequestMapping(value = ItemReportRequest.BOM_RECURSION_CHECK_URL, method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json")
    private ItemExplosionResponse handleItemWhereUsed(@RequestBody ItemReportRequest itemReportRequest) {
        var rValue = new ItemExplosionResponse();
        rValue.setResponseType(ResponseType.QUERY);

        var isItemIdInWhereUsed = bomNavigation.isItemIdInWhereUsed(itemReportRequest.getChildId(),
                itemReportRequest.getParentId() );

        if ( itemReportRequest.getChildId() != itemReportRequest.getParentId() ) {
            var message = "Parent and proposed child are same item.  " + itemReportRequest.getChildId() ;
            rValue.addError( new ErrorLine( 0, "Ancestor matches child", message ));
            logger.error( message );
            return rValue;
        }

        if ( isItemIdInWhereUsed ) {
            var message = "Proposed component " + itemReportRequest.getChildId() + " also appears as a parent.";
            rValue.addError( new ErrorLine( 0, "Ancestor Discovered", message ));
            logger.error( message );
        } else {
            logger.info("Proposed component {} does not appear as an ancestor.", itemReportRequest.getChildId());
        }

       return rValue;
    }

    @CrossOrigin
    @RequestMapping(value = ItemReportRequest.SHOW_ALL_ITEMS_URL, method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json")
    private ItemExplosionResponse showAllItems(@RequestBody ItemReportRequest itemReportRequest) {
        var rValue = new ItemExplosionResponse();
        rValue.setResponseType(ResponseType.QUERY);
        rValue.setData( itemReportService.generateAllItemReport( itemRepository ) );
        return rValue;
    }

}

