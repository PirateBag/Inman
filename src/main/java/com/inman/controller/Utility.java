package com.inman.controller;

import org.springframework.dao.DataIntegrityViolationException;

import static com.inman.controller.Bom.UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;

public class Utility {
    public static String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
        var detailedMessage = dataIntegrityViolationException.getMessage();
        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
        }
        return detailedMessage;
    }
}
