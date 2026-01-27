package com.inman.controller;

import com.inman.model.response.ResponsePackage;
import com.inman.model.rest.ErrorLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class LoggingUtility {
    // Returns a logger for the calling class
    public static Logger getLogger() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        return LoggerFactory.getLogger(className);
    }

    public static void outputInfoToLog( String message ) {
        LoggingUtility.getLogger().info(message);
    }

    public static void outputInfoToResponse(HttpStatus httpStatus, String message, ResponsePackage<?> responsePackage) {
        getLogger().warn(message);
        responsePackage.getErrors().add(new ErrorLine(httpStatus, message));
    }

    public static void outputErrorAndThrow(HttpStatus httpStatus, String message, ResponsePackage<?> responsePackage ) {
        getLogger().error(message);
        responsePackage.getErrors().add(new ErrorLine( httpStatus, message));
        throw new RuntimeException(message);
    }
}
