package com.bobocode.bibernate;

public interface AttributeConverter<T, R> {
    R convertToDatabaseColumn(T attribute);
    T convertToEntityAttribute(R dbData);
}
