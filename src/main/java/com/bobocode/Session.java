package com.bobocode;

import java.util.List;
import java.util.Map;

public interface Session {
    <T> T find(Class<T> type, Object primaryKey);
    <T> List<T> findAll(Class<T> type);
    <T> List<T> findAll(Class<T> type, Map<String, Object> properties);
    <T> void save(T entity);
    <T> void update(T entity);
    <T> void delete(T entity);

    void flush();
    Transaction beginTransaction();
    Transaction commitTransaction();
    Transaction rollbackTransaction();
}
