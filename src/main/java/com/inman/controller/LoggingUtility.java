package com.inman.controller;
import com.inman.model.response.ResponsePackage;
import com.inman.model.rest.ErrorLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUtility {
    // Returns a logger for the calling class
    public static Logger getLogger() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println( "This might be the class name:" + className);
        return LoggerFactory.getLogger(className);
    }

    public static void outputInfo(String message, ResponsePackage<?> responsePackage, Logger logger) {
        LoggingUtility.getLogger().info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
    }

    public static void outputErrorAndThrow(String message, ResponsePackage<?> responsePackage, Logger logger) {
        logger.error(message);
        responsePackage.getErrors().add(new ErrorLine(responsePackage.getErrors().size(), message));
        throw new RuntimeException(message);
    }


}
