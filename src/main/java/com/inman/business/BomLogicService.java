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
public class BomLogicService {
    private ItemRepository itemRepository;
    private BomPresentRepository bomPresentRepository;

    static Logger logger = LoggerFactory.getLogger(BomLogicService.class);

    @Autowired
    public BomLogicService(ItemRepository itemRepository, BomPresentRepository bomPresentRepository) {
        this.itemRepository = itemRepository;
        this.bomPresentRepository = bomPresentRepository;

    }

    public boolean isItemIdInWhereUsed(long newProposedNewChild, long parentId) {
        logger.info("Searching for parents of " + parentId + " in " + newProposedNewChild);
        BomPresent[] parents = bomPresentRepository.findByChildId(parentId);

        if (parents.length == 0) {
            logger.info("No parents of " + newProposedNewChild);
            return false;
        }

        for (BomPresent bom : parents) {
            logger.info("Trying " + bom.getParentId());
            if (bom.getParentId() == newProposedNewChild) {
                logger.info("Found parent: " + bom.getParentId() + " exiting.");
                return true;
            }
            return isItemIdInWhereUsed(newProposedNewChild, bom.getParentId());
        }
        return false;
    }

    public void ancestorsOf(long itemId, Set<BomPresent> listOfAncestors) {
        assert listOfAncestors != null;
        assert itemId < 1;

        logger.info("Searching for ancestoers of " + itemId);

        BomPresent[] parents = bomPresentRepository.findByChildId(itemId);

        if (parents.length == 0) {
            logger.info("No parents of " + itemId);
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
    public void updateMaxDepthOf(Long componentId, ArrayList<Text> texts) {
        Optional<Item> componentFromRepo = itemRepository.findById(componentId);
        String message;
        if (componentFromRepo.isEmpty()) {
            outputInfo( "ItemId " + componentId + " not found", texts );
            return;
        }
        Item component = componentFromRepo.get();
        logger.info( "Item is: " + component  );
        updateThisComponentBasedOnParents(component, texts);

        if (component.getSourcing().equals(Item.SOURCE_PUR)) {
            message = component + " is " + component.getSourcing() + ".  Stopping.";
            logger.info(message);
            return;
        }
        BomPresent[] childrenOfComponent = bomPresentRepository.findByParentId(componentId);

        if (childrenOfComponent.length == 0) {
            message = component + " is childless";
            logger.info(message);
        } else {
            logger.info( "Found the following children of " + component );

            for (BomPresent child : childrenOfComponent) {
                outputInfoNoResponse( "----" + child, texts);
                updateMaxDepthOf(child.getChildId(), texts);
            }
        }
    }

    private void updateThisComponentBasedOnParents(Item thisComponent, ArrayList<Text> texts) {
        long[] parentsOf = itemRepository.findParentsFor(thisComponent.getId());
        logger.info("possible parents of " + thisComponent + " include " + Arrays.toString(parentsOf));

        int maxDepthFromParents = 0;
        for (long parentOf : parentsOf) {
            Item parent = itemRepository.findById(parentOf);
            maxDepthFromParents = Math.max(maxDepthFromParents, parent.getMaxDepth() + 1 );
            outputInfoNoResponse("----Parent " + parent + " tested for maxdepth " + maxDepthFromParents, texts);
        }

        if (maxDepthFromParents == 0) {
            outputInfoNoResponse( "No parents or all parents at depth of zero"   , texts);
            return;
        }

        if (thisComponent.getMaxDepth() >= maxDepthFromParents) {
            outputInfoNoResponse("Component " + thisComponent + " is greater than parent.  No change.", texts);
            return;
        }

        outputInfo(thisComponent + " depth changing from " + thisComponent.getMaxDepth() + " to " + maxDepthFromParents, texts);

        thisComponent.setMaxDepth(maxDepthFromParents);
        itemRepository.save(thisComponent);
    }

    private void outputInfo(String message, ArrayList<Text> texts) {
        logger.info(message);
        texts.add(new Text(message));
    }
    private void outputInfoNoResponse(String message, ArrayList<Text> texts) {
        logger.info(message);
    }

}
