package com.inman.controller;

import com.inman.model.response.ResponsePackage;
import com.inman.model.rest.ErrorLine;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.inman.controller.Bom.UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;

public class Utility {
    public static String _DATE_FORMAT = "yyyy-MMdd";
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(_DATE_FORMAT); // Define the format

    @NotNull
    public static String normalize(String str) {
        if (str == null) {
            return "";
        }
        ;
        return str;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String generateErrorMessageFrom(DataIntegrityViolationException dataIntegrityViolationException) {
        var detailedMessage = dataIntegrityViolationException.getMessage();
        if (detailedMessage.contains(UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION)) {
            return UNIQUE_INDEX_OR_PRIMARY_KEY_VIOLATION;
        }
        return detailedMessage;
    }

    public static boolean isDateFormatValid(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);

        } catch (DateTimeParseException dtpe) {
            return false;
        }
        return true;
    }

    public static void outputInfo(String message, ResponsePackage<?> responsePackage, Logger logger) {
        logger.info(message);
        responsePackage.getErrors().add(new ErrorLine(1, message));
    }

    public static void outputErrorAndThrow(String message, ResponsePackage<?> responsePackage, Logger logger) {
        logger.error(message);
        responsePackage.getErrors().add(new ErrorLine(responsePackage.getErrors().size(), message));
        throw new RuntimeException(message);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
