package com.inman.business;

import com.inman.controller.OrderLineItemController;
import com.inman.entity.OrderLineItem;
import com.inman.entity.Text;
import com.inman.model.response.TextResponse;
import com.inman.repository.OrderLineItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.inman.controller.OrderLineItemController.OrderLineItem_AllOrders;

@Service
public class OrderLineItemService {
    private static final Logger logger = LoggerFactory.getLogger(OrderLineItemService.class);

    OrderLineItemRepository orderLineItemRepository;

    public OrderLineItemService(OrderLineItemRepository orderLineItemRepository) {
        this.orderLineItemRepository = orderLineItemRepository;
    }

//    private String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
//        var detailedMessage = dataIntegrityViolationException.getMessage();
//        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
//            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
//        }
//        return detailedMessage;
//    }
//
//    private void updateMaxDepthOf(BomPresent updatedBom ) {
//        Item component = itemRepository.findById(updatedBom.getChildId());
//        Item parent = itemRepository.findById(updatedBom.getParentId());
//
//        if (component.getMaxDepth() <= parent.getMaxDepth()) {
//            int newMaxDepth = parent.getMaxDepth() + 1;
//            logger.info("{} depth changing from {} to {}" + 1, component.getId(), component.getMaxDepth(), parent.getMaxDepth());
//            component.setMaxDepth(newMaxDepth);
//            itemRepository.save(component);
//            return;
//        }
//        logger.info("{} depth not changing {} to {}" + 1, component.getId(), component.getMaxDepth(), parent.getMaxDepth());
//    }
//
//
//    @CrossOrigin
//    @RequestMapping(value = BomPresentSearchRequest.all, method = RequestMethod.POST)
//    public ResponseEntity<?> bomPresentFindAll(@RequestBody BomPresentSearchRequest xBomPresentSearchRequest) {
//        BomPresent[] boms;
//        if (xBomPresentSearchRequest.getIdToSearchFor() == 0) {
//            boms = bomPresentRepository.findAll().toArray(new BomPresent[0]);
//        } else {
//            BomPresent bom = bomPresentRepository.findById(xBomPresentSearchRequest.getIdToSearchFor());
//            boms = new BomPresent[1];
//            boms[0] = bom;
//        }
//
//        BomResponse responsePackage = new BomResponse();
//        responsePackage.setData((ArrayList<BomPresent>) Arrays.asList(boms));
//
//        return ResponseEntity.ok().body(responsePackage);
//    }
//
//
//	/*
//	@CrossOrigin
//	@RequestMapping( value = BomUpdate.INVALID_COMPONENTS_URL, method=RequestMethod.POST )
//	public ResponseEntity<?> bomFindInvalidComponents( @RequestBody BomPresent[] proposedComponents  )
//	{
//		ResponsePackage responsePackage = bomNavigation.isItemIdInWhereUsed( proposedComponents );
//
//		return ResponseEntity.ok().body( responsePackage );
//	}
//	*/
//
//
//    private void change(BomPresent updatedBom, BomResponse bomResponse, int lineNumber) {
//        var oldBom = bomRepository.findById(updatedBom.getId());
//        String message = "";
//        if (oldBom.isEmpty()) {
//            message = "Unable to retrieve the original Bom instance for id " + updatedBom.getId();
//            bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
//            logger.error(message);
//            throw new RuntimeException(message);
//        }
//
//        if (updatedBom.getQuantityPer() == oldBom.get().getQuantityPer()) {
//            message = "Bom " + updatedBom.getId() + " quantityPer field did not change.";
//            logger.warn(message);
//            bomResponse.addError(new ErrorLine(lineNumber, "0001", message));
//        } else {
//            logger.info("Bom " + updatedBom.getId() + " quantityPer was updated from " + oldBom.get().getQuantityPer() + " to " + updatedBom.getQuantityPer());
//            oldBom.get().setQuantityPer(updatedBom.getQuantityPer());
//
//            bomRepository.save(oldBom.get());
//            var refreshedBom = bomPresentRepository.findById(updatedBom.getId());
//            refreshedBom.setActivityState(ActivityState.CHANGE);
//            bomResponse.getData().add(refreshedBom);
//        }
//
//        updateMaxDepthOf(updatedBom );
//    }
//
//    private void insert(BomPresent updatedBom, BomResponse bomResponse, int lineNumber) {
//        String message;
//        com.inman.entity.Bom bomToBeInserted = new com.inman.entity.Bom(updatedBom.getParentId(), updatedBom.getChildId(), updatedBom.getQuantityPer());
//        com.inman.entity.Bom insertedBom = null;
//        try {
//            bomLogicService.isItemIdInWhereUsed(updatedBom.getParentId(),
//                    bomToBeInserted.getChildId());
//            insertedBom = bomRepository.save(bomToBeInserted);
//            logger.info( "insertedBom" + insertedBom );
//            var refreshedBom = bomPresentRepository.byParentIdChildId(insertedBom.getParentId(), bomToBeInserted.getChildId());
//            if (refreshedBom == null) {
//                message = "Bom " + insertedBom.getId() + " unable to re-retrieve inserted BOM ";
//                logger.error(message);
//                bomResponse.addError(new ErrorLine(lineNumber, message));
//                return;
//            }
//
//            refreshedBom.setActivityState(ActivityState.INSERT);
//            bomResponse.getData().add(refreshedBom);
//            updateMaxDepthOf(updatedBom );
//
//        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
//            message = "Unable to insert " + bomToBeInserted.getParentId() + ":" +
//                    bomToBeInserted.getChildId() + " due to " +
//                    generateErrorMessageFrom(dataIntegrityViolationException);
//            logger.error(message);
//            bomResponse.addError(new ErrorLine(lineNumber, message));
//        }
//    }

    public TextResponse orderReport( int orderId ) {
        if (orderId != OrderLineItem_AllOrders) {
            throw new RuntimeException("Illegal order id of " + orderId);
        }
        TextResponse textResponse = new TextResponse();
        List<com.inman.entity.OrderLineItem> reportList = orderLineItemRepository.findAll();

        logger.info(OrderLineItem.header );
        for (com.inman.entity.OrderLineItem orderLineItem : reportList) {
            logger.info( orderLineItem.toString() );
            textResponse.getData().add( new Text(orderLineItem.toString() ) );
        }
        return textResponse;
    }
}
