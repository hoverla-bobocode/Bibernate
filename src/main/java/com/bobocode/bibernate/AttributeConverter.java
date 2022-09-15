package com.bobocode.bibernate;

public interface AttributeConverter<T, R> {
    T convertToEntityAttribute(R dbData);
}
