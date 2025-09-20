package com.inman.controller;

import com.inman.model.request.AdjustmentCrudRequest;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.AdjustmentCrudResponse;
import com.inman.model.response.TextResponse;
import com.inman.service.AdjustmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Configuration
@RestController
public class AdjustmentController {
    public final static String ADJUSTEMENT_CRUDD = "adjustment/crud";
    public final static String ADJUSTEMENT_REPORT_ALL = "adjustment/reportAll";
    Logger logger = LoggerFactory.getLogger(AdjustmentController.class);
    AdjustmentService adjustmentService;

    public AdjustmentController( AdjustmentService adjustmentService ) {
        this.adjustmentService = adjustmentService;
    }

    @CrossOrigin
    @RequestMapping(value = ADJUSTEMENT_CRUDD, method = RequestMethod.POST)
    public ResponseEntity<?> apBasic (@RequestBody AdjustmentCrudRequest adjustmentCrudRequest  ) {
        AdjustmentCrudResponse responsePackage = new AdjustmentCrudResponse();

        try {
            adjustmentService.crud(adjustmentCrudRequest, responsePackage);
            return ResponseEntity.ok().body( responsePackage );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body( responsePackage );
        }

    }

    @CrossOrigin
    @RequestMapping(value = ADJUSTEMENT_REPORT_ALL, method = RequestMethod.POST )
    public ResponseEntity<?> apBasic (@RequestBody GenericSingleId genericSingleId  ) {
        TextResponse textResponse = new TextResponse();

        adjustmentService.reportAll(genericSingleId.idToSearchFor(), textResponse );

        return ResponseEntity.badRequest().body( textResponse );
    }

}

