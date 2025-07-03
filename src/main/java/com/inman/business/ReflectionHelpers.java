package com.inman.business;

import java.lang.reflect.Field;
import java.util.*;

public class ReflectionHelpers {

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

    /**
     * Return a list of field names for a given class:
     * public members are ommitted.
     * public members from the parent are included.
     * Confusing:  Look up differences betwees the Class. getField and getDeclaredFields.
     *
     * @param object object with fields.
     * @return Set with field names.
     */
    public static <T> Map<String, Field> setOfFields(T object ) {
        Map<String, Field> rValue = new TreeMap<>();

        if (object == null ) {
            throw new IllegalArgumentException("object must be non-null");
        }
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                clazz.getField(field.getName());
            } catch (NoSuchFieldException e) {
                //  This is OK.
                //  If field is not accessible through getName(), then add it.
                rValue.put(field.getName(), field);
            }
        }

        Class<?> superClass = clazz.getSuperclass();

        try {
            var  field =superClass.getDeclaredField( "id" );
            rValue.put( "id", field );
            field = superClass.getDeclaredField( "activityState" );
            rValue.put( "activityState", field );
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return rValue;
    }

    public static int  applyMapOfChanges(Map<String, Object[]> changeMap) {
        int count = 1;
        for (Map.Entry<String, Object[]> entry : changeMap.entrySet()) {
            Object[] oldValue = entry.getValue();
            oldValue[ 0 ] = oldValue[ 1 ];
            count++;
        }
        return count;
    }
}

