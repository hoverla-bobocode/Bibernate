package com.bobocode.bibernate.converter;

/**
 * Class to converting JDBC based types to Java types.
 */
public interface AttributeConverter<T> {

    /**
     * Converts provided JDBC-type to a generic <T> type
     * @param dbData - value to be converted
     * @return - instance of T type
     */
    T convertToEntityAttribute(Object dbData);

    /**
     * Checks if provided value can be converted by the current converter
     */
    boolean isConvertable(Object value);
}
