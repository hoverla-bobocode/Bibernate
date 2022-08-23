package com.bobocode;

public interface AttributeConverter<T, R> {
    R convertToDatabaseColumn(T attribute);
    T convertToEntityAttribute(R dbData);
}
