package com.inman.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CompareObjects {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Compares two objects of the same type and returns a map of field differences.
     *
     * @param oldObject the original object
     * @param newObject the updated object
     * @param <T>       the type of the objects
     * @return Map with field names as keys and [oldValue, newValue] as values
     */
    public static <T> Map<String, Object[]> compareObjects(T oldObject, T newObject) {
        Map<String, Object[]> differences = new HashMap<>();

        if (oldObject == null || newObject == null) {
            throw new IllegalArgumentException("Both objects must be non-null");
        }

        if (!oldObject.getClass().equals(newObject.getClass())) {
            throw new IllegalArgumentException("Objects must be of the same type");
        }

        Class<?> clazz = oldObject.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);

                if (oldValue == null && newValue == null) {
                    continue;
                }

                if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
                    differences.put(field.getName(), new Object[]{oldValue, newValue});
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }

        return differences;
    }
}

