package com.inman.service;

import com.inman.controller.LoggingUtility;
import com.inman.controller.Messages;
import com.inman.entity.BomPresent;
import com.inman.entity.Item;
import com.inman.entity.Text;
import com.inman.model.response.ResponsePackage;
import com.inman.repository.BomPresentRepository;
import com.inman.repository.ItemRepository;
import enums.SourcingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static com.inman.controller.LoggingUtility.outputInfoToLog;

@Service
public class BomLogicService {
    private ItemRepository itemRepository;
    private BomPresentRepository bomPresentRepository;

    @Autowired
    public BomLogicService(ItemRepository itemRepository, BomPresentRepository bomPresentRepository) {
        this.itemRepository = itemRepository;
        this.bomPresentRepository = bomPresentRepository;
    }

    /**
     *
     * @param newProposedNewChild
     * @param parentId
     * @param bomResponse
     * @return true if the the proposed child is already used on a parent.
     */
    public boolean isItemIdInWhereUsed(long newProposedNewChild, long parentId, ResponsePackage<?> bomResponse ) {
        outputInfoToLog( "Searching for parents of " + parentId + " in " + newProposedNewChild);

        BomPresent[] parents = bomPresentRepository.findByChildId(parentId);

        if (parents.length == 0) {
            outputInfoToLog( "There are no BOMs that have %d as a child." + parentId);
            return false;
        }

        for (BomPresent bom : parents) {
            outputInfoToLog("Trying " + bom.getParentId());
            if (bom.getParentId() == newProposedNewChild) {
                outputInfoToLog("Found parent: " + bom.getParentId() + " exiting.");
                return true;
            }
            return isItemIdInWhereUsed(newProposedNewChild, bom.getParentId(), bomResponse );
        }
        return false;
    }

    /**
     * Given a parent id, see if the proposed child has the same ID as the existing children.
     * @param newProposedNewChild
     * @param parentId
     * @param bomResponse Error text of any messages placed here.
     * @return Does not return, but does throw
    */
    void areAnyOfTheSiblingsAlreadyTheSame(long newProposedNewChild, long parentId, ResponsePackage<?> bomResponse ){
        String rValue = "";
        BomPresent[] siblings = bomPresentRepository.findByParentId(parentId);
        for (BomPresent sibling : siblings) {
            if (sibling.getChildId() == newProposedNewChild) {
                LoggingUtility.outputErrorAndThrow( Messages.DUPLICATE_SIBLING.httpStatus(), Messages.DUPLICATE_SIBLING.text().formatted(newProposedNewChild, parentId ), bomResponse );
            }
        }
    }

    public void ancestorsOf(long itemId, Set<BomPresent> listOfAncestors) {
        assert listOfAncestors != null;
        assert itemId < 1;

        outputInfoToLog( "Searching for ancestoers of " + itemId);

        BomPresent[] parents = bomPresentRepository.findByChildId(itemId);

        if (parents.length == 0) {
            outputInfoToLog("No parents of " + itemId);
            return;
        }

        for (BomPresent bom : parents) {

            outputInfoToLog("Trying " + bom.getParentId());

            if (!listOfAncestors.add(bom)) {
                //  No need to continue if we have already encountered this ancesteor
                outputInfoToLog("Encountered ancestor " + bom.getId());
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
            outputInfoToLog( "ItemId " + componentId + " not found" );
            return;
        }
        Item component = componentFromRepo.get();
        outputInfoToLog( "Item is: " + component  );
        updateThisComponentBasedOnParents(component, texts);

        if (component.getSourcing() == SourcingType.PUR ) {
            message = component + " is " + component.getSourcing() + ".  Stopping.";
            outputInfoToLog(message);
            return;
        }
        BomPresent[] childrenOfComponent = bomPresentRepository.findByParentId(componentId);

        if (childrenOfComponent.length == 0) {
            message = component + " is childless";
            outputInfoToLog(message);
        } else {
            outputInfoToLog( "Found the following children of " + component );

            for (BomPresent child : childrenOfComponent) {
                outputInfoToLog( "----" + child );
                updateMaxDepthOf(child.getChildId(), texts);
            }
        }
    }

    private void updateThisComponentBasedOnParents(Item thisComponent, ArrayList<Text> texts) {
        long[] parentsOf = itemRepository.findParentsFor(thisComponent.getId());
        outputInfoToLog("possible parents of " + thisComponent + " include " + Arrays.toString(parentsOf));

        int maxDepthFromParents = 0;
        for (long parentOf : parentsOf) {
            Item parent = itemRepository.findById(parentOf);
            maxDepthFromParents = Math.max(maxDepthFromParents, parent.getMaxDepth() + 1 );
            outputInfoToLog("----Parent " + parent + " tested for maxdepth " + maxDepthFromParents );
        }

        if (maxDepthFromParents == 0) {
            outputInfoToLog( "No parents or all parents at depth of zero" );
            return;
        }

        if (thisComponent.getMaxDepth() >= maxDepthFromParents) {
            outputInfoToLog("Component " + thisComponent + " is greater than parent.  No change." );
            return;
        }

        outputInfoToLog(thisComponent + " depth changing from " + thisComponent.getMaxDepth() + " to " + maxDepthFromParents );

        thisComponent.setMaxDepth(maxDepthFromParents);
        itemRepository.save(thisComponent);
    }
}
