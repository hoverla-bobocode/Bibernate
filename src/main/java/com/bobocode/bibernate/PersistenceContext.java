package com.bobocode.bibernate;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PersistenceContext {

    private final Map<EntityKey, Object> entityCacheMap = new HashMap<>();
    private final Map<EntityKey, Object[]> entitySnapshotMap = new HashMap<>();

    public <T> Optional<T> getEntity(Class<T> type, Object key) {
        EntityKey entityKey = new EntityKey(type, key);
        Object entity = entityCacheMap.get(entityKey);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(type.cast(entity));
    }

    public void putEntity(Object entity, Object key) {
        Class<?> type = entity.getClass();
        EntityKey entityKey = new EntityKey(type, key);
        entityCacheMap.put(entityKey, entity);
    }

    public void putEntitySnapshot(Object key, Object entity) {
        Class<?> type = entity.getClass();
        EntityKey entityKey = new EntityKey(type, key);
        Object[] values = Arrays.stream(entity.getClass().getDeclaredFields()).sorted(Comparator.comparing(Field::getName))
                .map(f -> Util.getValueFromField(f, entity))
                .toArray();
        entitySnapshotMap.put(entityKey, values);
    }

    public Map<Object, Map<String, Object>> getUpdatedEntitiesColumnsMap() {
        return entityCacheMap.entrySet()
                .stream()
                .map(a -> new AbstractMap.SimpleEntry<>(a.getValue(), getUpdatedColumnsWithValues(a.getKey(), a.getValue())))
                .filter(a -> !a.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Object> getUpdatedColumnsWithValues(EntityKey entityKey, Object entity) {
        Object[] snapshotValues = entitySnapshotMap.get(entityKey);
        if (snapshotValues == null) {
            return Map.of();
        }
        List<Field> entityFields = Arrays.stream(entity.getClass().getDeclaredFields())
                .sorted(Comparator.comparing(Field::getName))
                .toList();

        return getUpdatedColumnsWithValuesMap(entity, snapshotValues, entityFields);
    }

    private static Map<String, Object> getUpdatedColumnsWithValuesMap(Object entity, Object[] snapshotValues, List<Field> entityFields) {
        Map<String, Object> updatedColumnsToValuesMap = new HashMap<>();
        for (int i = 0; i < entityFields.size(); i++) {
            Field entityField = entityFields.get(i);
            Object entityFieldValue = Util.getValueFromField(entityField, entity);
            Object snapshotFieldValue = snapshotValues[i];
            if (Objects.equals(entityFieldValue, snapshotFieldValue)) {
                continue;
            }
            updatedColumnsToValuesMap.put(Util.getColumnName(entityField), entityFieldValue);
        }
        return updatedColumnsToValuesMap;
    }

    public <T> void evict(T entity, Object key) {
        EntityKey entityKey = new EntityKey(entity.getClass(), key);
        entityCacheMap.remove(entityKey, entity);
        entitySnapshotMap.remove(entityKey);
    }

    public record EntityKey(Class<?> type, Object key) {

    }
}
