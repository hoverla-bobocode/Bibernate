package com.bobocode.bibernate.converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LocalDateTimeTypeConverter implements AttributeConverter<LocalDateTime> {

    @Override
    public LocalDateTime convertToEntityAttribute(Object dbData) {
        Timestamp timestamp = Timestamp.valueOf(dbData.toString());
        return timestamp.toLocalDateTime();
    }

    @Override
    public boolean isConvertable(Object value) {
        if (value != null) {
            Class<?> valuesType = value.getClass();
            return valuesType.isAssignableFrom(Timestamp.class) ||
                    valuesType.getSuperclass().isAssignableFrom(Timestamp.class);
        }
        return false;
    }
}