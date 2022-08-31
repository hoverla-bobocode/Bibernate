package com.bobocode.bibernate;

import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static <T> String getTableName(Class<T> type) {
        if (type.isAnnotationPresent(Table.class)) {
            return type.getAnnotation(Table.class).value();
        }
        return type.getSimpleName().toLowerCase();
    }

    public static String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class).value();
        }
        return field.getName();
    }
}
