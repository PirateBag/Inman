package com.inman.controller;

import com.inman.business.AutomatedPlanningService;
import com.inman.model.request.GenericSingleId;
import com.inman.model.response.ResponseType;
import com.inman.model.response.TextResponse;
import com.inman.model.rest.ErrorLine;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Configuration
@RestController
public class AutomatedPlanningController {
    public final static String AutomatedPlan_Url = "ap/basic";
    Logger logger = LoggerFactory.getLogger(AutomatedPlanningController.class);

    AutomatedPlanningService automatedPlanningService;

    @CrossOrigin
    @RequestMapping(value = AutomatedPlan_Url, method = RequestMethod.GET)
    public ResponseEntity<?> apBasic (@RequestParam String typeOfPlanning  ) {
        TextResponse responsePackage = new TextResponse();
        processPlanning(responsePackage);
        return ResponseEntity.badRequest().body( responsePackage );
    }

    @CrossOrigin
    @RequestMapping(value = AutomatedPlan_Url, method = RequestMethod.POST)
    public ResponseEntity<?> apBasic (@RequestBody GenericSingleId genericSingleId  ) {
        TextResponse responsePackage = new TextResponse();
        processPlanning(responsePackage);
        return ResponseEntity.badRequest().body( responsePackage );
    }

    private void processPlanning(TextResponse responsePackage) {
        try {
            automatedPlanningService.basic( "", responsePackage);
            responsePackage.setResponseType(ResponseType.MULTILINE );

            if (responsePackage.getData().isEmpty()) {
                var message = "No items were processed, either due to errors or no actionable inputs.";
                logger.info(message);
                responsePackage.getErrors().add(new ErrorLine(1, message));
            }

        } catch ( Exception exception ) {
            logger.info( "Encountered runtime exception, check response message for details.");
        }
    }


    @Autowired
    public AutomatedPlanningController(AutomatedPlanningService automatedPlanningService ) {
        this.automatedPlanningService  =  automatedPlanningService;
    }

}

