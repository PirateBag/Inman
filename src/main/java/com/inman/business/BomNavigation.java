package com.inman.business;

import com.inman.entity.Item;
import com.inman.entity.Text;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.inman.entity.BomPresent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
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

    @Transactional
    public void  updateMaxDepthOf(Long componentId, ArrayList<Text> texts) {
        Optional<Item> componentFromRepo = itemRepository.findById( componentId );
        String message;
        if ( componentFromRepo.isEmpty() ) {
            message = "Item with id " + componentId + " not found";
            logger.error( message );
            texts.add( new Text( message ) );
            return;
        }
        Item component = componentFromRepo.get();

        long [] parentsOf = itemRepository.findParentsFor( componentId );
        logger.info( "possible parents include " +	Arrays.toString( parentsOf ) );

        for ( long parentOf : parentsOf ) {
            Item parent = itemRepository.findById(parentOf);

            if (component.getMaxDepth() > parent.getMaxDepth()) {
                message = "Component " + component + " depth not changing.  Depth appears to be upto date";
                texts.add( new Text( message ) );
                logger.info( message );
                return;
            }

            int newMaxDepth = parent.getMaxDepth() + 1;
            message = component.getId() + " depth changing from " + component.getMaxDepth() + " to " + newMaxDepth;
            texts.add( new Text( message ) );
            logger.info( message );
            component.setMaxDepth(newMaxDepth);
            itemRepository.save(component );

            if ( component.getSourcing().equals( Item.SOURCE_PUR ) ) {
                message = component.getId() + " is " + component.getSourcing() + ".  Stopping.";
                texts.add( new Text( message ) );
                logger.info( message);
                return;
            }
            BomPresent[] childrenOfComponent = bomPresentRepository.findByParentId( componentId );
            message = "Found the following children of " + component.getId();
            texts.add( new Text( message ) );
            logger.info( message);


            for ( BomPresent child : childrenOfComponent ) {
                message = "   " + child;
                texts.add( new Text( message ) );
                logger.info( message);
            }
        }
    }
}
