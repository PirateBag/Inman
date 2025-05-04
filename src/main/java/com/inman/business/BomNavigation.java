package com.inman.business;

import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.inman.entity.BomPresent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

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

    public void ancestorsOf( long itemId, Set<BomPresent> listOfAncestors ) {
        assert listOfAncestors != null;
        assert itemId < 1;

        logger.info( "Searching for ancestoers of " + itemId );

        BomPresent[] parents = bomPresentRepository.findByChildId( itemId );

        if (parents.length == 0) {
            logger.info("No parents of " + itemId );
            return;
        }

        for (BomPresent bom : parents) {

            logger.info("Trying " + bom.getParentId());

            if (!listOfAncestors.add(bom)) {
                //  No need to continue if we have already encountered this ancesteor
                logger.info("Encountered ancestor " + bom.getId());
                return;
            }

            ancestorsOf(bom.getParentId(), listOfAncestors);
        }
    }
}
