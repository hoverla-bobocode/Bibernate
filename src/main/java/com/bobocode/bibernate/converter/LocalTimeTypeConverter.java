package com.bobocode.bibernate.converter;

import java.sql.Timestamp;
import java.time.LocalTime;

public class LocalTimeTypeConverter implements AttributeConverter<LocalTime> {

    @Override
    public LocalTime convertToEntityAttribute(Object dbData) {
        Timestamp timestamp = Timestamp.valueOf(dbData.toString());
        return timestamp.toLocalDateTime().toLocalTime();
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
