package com.inman.controller;

import com.inman.entity.Adjustment;
import com.inman.model.request.AdjustmentCrudRequest;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.AdjustmentCrudResponse;
import com.inman.model.response.TextResponse;
import com.inman.service.AdjustmentService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Configuration
@RestController
public class AdjustmentController {
    public final static String ADJUSTMENT_CRUDD = "adjustment/crud";
    public final static String ADJUSTMENT_REPORT_ALL = "adjustment/reportAll";
    public final static String ADJUSTMENT_REPORT_QUERY = "adjustment/query";
    AdjustmentService adjustmentService;

    public AdjustmentController( AdjustmentService adjustmentService ) {
        this.adjustmentService = adjustmentService;
    }

    @CrossOrigin
    @RequestMapping(value = ADJUSTMENT_CRUDD, method = RequestMethod.POST)
    public ResponseEntity<?> adjustment_show_post (@RequestBody AdjustmentCrudRequest adjustmentCrudRequest  ) {
        AdjustmentCrudResponse responsePackage = new AdjustmentCrudResponse();

        try {
            adjustmentService.crud(adjustmentCrudRequest, responsePackage);
            return ResponseEntity.ok().body( responsePackage );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body( responsePackage );
        }
    }



    @CrossOrigin
    @RequestMapping(value = ADJUSTMENT_REPORT_ALL, method = RequestMethod.POST )
    public ResponseEntity<?> adjustment_show_put (@RequestBody GenericSingleId genericSingleId ) {
        TextResponse textResponse = new TextResponse();

        adjustmentService.reportAll( genericSingleId.idToSearchFor(), textResponse );

        return ResponseEntity.ok().body( textResponse );
    }

    @CrossOrigin
    @RequestMapping(value = ADJUSTMENT_REPORT_QUERY, method = RequestMethod.POST )
    public ResponseEntity<?> adjustment_query (@RequestBody AdjustmentCrudRequest adjustmentCrudRequest   ) {
        try {
            AdjustmentCrudResponse adjustmentCrudResponse = new AdjustmentCrudResponse();
            adjustmentCrudResponse.setData((ArrayList<Adjustment>) adjustmentService.query(adjustmentCrudRequest));
            return ResponseEntity.ok().body( adjustmentCrudResponse );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body( e.getMessage() );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body( e.getMessage() );
        }
    }

    @CrossOrigin
    @RequestMapping(value = "adjustment/lastQuerySql", method = RequestMethod.GET )
    public ResponseEntity<String> getLastQuerySql() {
        return ResponseEntity.ok().body(adjustmentService.getLastQuerySql());
    }
}

