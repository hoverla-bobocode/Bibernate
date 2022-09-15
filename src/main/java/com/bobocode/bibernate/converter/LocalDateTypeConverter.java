package com.bobocode.bibernate.converter;

import java.sql.Date;
import java.time.LocalDate;

public class LocalDateTypeConverter implements AttributeConverter<LocalDate> {

    @Override
    public LocalDate convertToEntityAttribute(Object dbData) {
        Date date = Date.valueOf(dbData.toString());
        return date.toLocalDate();
    }

    @Override
    public boolean isConvertable(Object value) {
        if (value != null) {

            Class<?> valuesType = value.getClass();
            return valuesType.isAssignableFrom(java.util.Date.class) ||
                    valuesType.getSuperclass().isAssignableFrom(java.util.Date.class);
        }
        return false;
    }
}

