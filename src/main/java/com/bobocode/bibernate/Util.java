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

    /**
     * Returns the name of the table provided as value in {@link Table} annotation
     * @return {@link String} table name
     */
    public static <T> String getTableName(Class<T> type) {
        if (type.isAnnotationPresent(Table.class)) {
            return type.getAnnotation(Table.class).value();
        }
        return type.getSimpleName().toLowerCase();
    }

    /**
     * Returns the name of the column provided as value in{@link Column} annotation
     */
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

    @SuppressWarnings("java:S3011")
    public static void setValueToField(Object value, Field field, Object entity) {
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new BibernateException("Failed to set value to field", e);
        }
    }

    public static Field getIdField(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow();
    }

    public static <T> T mergeEntities(T entity, T cachedInstance) {
        Field[] fields = entity.getClass().getDeclaredFields();
        Field[] cachedFields = cachedInstance.getClass().getDeclaredFields();
        Field idField = Util.getIdField(entity.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Field cachedField = cachedFields[i];
            if (idField.equals(field)) {
                continue;
            }
            setValueToField(Util.getValueFromField(field, entity), cachedField, cachedInstance);
        }
        return cachedInstance;
    }

    public static Object getIdFieldValue(Object entity) {
        Class<?> entityType = entity.getClass();
        return Util.getValueFromField(getIdField(entityType), entity);
    }
}
