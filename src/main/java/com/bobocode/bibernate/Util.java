package com.bobocode.bibernate;

import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;
import com.bobocode.bibernate.exception.BibernateException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;

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

    @SuppressWarnings("java:S3011")
    public static Object getValueFromField(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new BibernateException("Failed to get value from field", e);
        }
    }

    public static Field getIdField(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow();
    }
}
