package com.inman.business;

import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
@Service
public class BomNavigation {
    static Logger logger = LoggerFactory.getLogger(BomNavigation.class + "findUltimateParent" );

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BomPresentRepository bomPresentRepository;


    public boolean isItemIdInWhereUsed (long newProposedNewChild, long parentId ) {
        logger.info("Searching for parents of " + parentId + " in " + newProposedNewChild);
        BomPresent[] parents = bomPresentRepository.findByChildId(parentId );

        if (parents.length == 0) {
            logger.info("No parents of " + newProposedNewChild);
            return false;
        }

        for (BomPresent bom : parents) {
            logger.info("Trying " + bom.getParentId());
            if (bom.getParentId() == newProposedNewChild ) {
                logger.info( "Found parent: " + bom.getParentId() + " exiting." );
                return true;
            }
            return isItemIdInWhereUsed(newProposedNewChild, bom.getParentId() );
        }
        return false;

    }
}
