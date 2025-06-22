package com.inman.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.format.DateTimeFormatter;

import static com.inman.controller.Bom.UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;

public class Utility {
    public static String _DATE_FORMAT = "yyyy-MMdd";
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern( _DATE_FORMAT ); // Define the format

    @NotNull
    public static String normalize( String str ) {
        if ( str == null ) { return ""; };
        return str;
    }

    public static String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
        var detailedMessage = dataIntegrityViolationException.getMessage();
        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
        }
        return detailedMessage;
    }
}
