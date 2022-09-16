package com.bobocode.bibernate.converter;

import java.sql.Timestamp;
import java.time.ZonedDateTime;


public class ZonedDateTimeTypeConverter implements AttributeConverter<ZonedDateTime> {

    @Override
    public ZonedDateTime convertToEntityAttribute(Object dbData) {
        return ZonedDateTime.parse(dbData.toString());
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
