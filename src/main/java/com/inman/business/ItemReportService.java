package com.inman.business;

import com.inman.entity.Item;
import com.inman.entity.Text;
import com.inman.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ItemReportService {

    String lineContents = "%-10s  %-30s  %10.2f  %3s  %10d";
    String lineHeader = "%-10s  %-30s  %10s  %3s  %10s";

    static Logger logger = LoggerFactory.getLogger("controller: " + ItemReportService.class);

    public ArrayList<Text> generateAllItemReport(ItemRepository itemRepository) {
        ArrayList<Text> reportOutput = new ArrayList<>();

        var message = String.format(lineHeader,
                    "Summary", "Description", "Unit Cost", "Sourcing", "Depth");
        reportOutput.add( new Text(message));
        logger.info(message);

        List<Item> items = itemRepository.findAllByOrderBySummaryIdAsc();

        if ( items.isEmpty() )        {
            message = "There are no items";
            reportOutput.add( new Text( message ));
            logger.error(message);
            return reportOutput;
        }

        for (Item item : items) {
            message = Common.spacesForLevel(0) + String.format(lineContents,
                    item.getSummaryId(),
                    item.getDescription(),
                    item.getUnitCost(),
                    item.getSourcing(),
                    item.getMaxDepth());

            logger.info(message);
            reportOutput.add( new Text(message));
        }

        return reportOutput;
    }


}
