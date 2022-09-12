package com.bobocode.bibernate.session;

import com.bobocode.bibernate.transaction.Transaction;
import com.bobocode.bibernate.exception.EntityMappingException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Session is used to interact with persistence context.
 * This interface is API for creating and removing persistence entity instances, finding them by primary key
 * or other properties.
 */
public interface Session extends AutoCloseable {

    /**
     * Find entity by primary key. Searching entity of passed type and primary key.
     * If the entity instance is contained in the persistence context, it is returned from there.
     * @param type specifies class of entity
     * @param primaryKey value of primary key for filtering results
     * @return found entity optional
     * @param <T> specifies type of entity
     *
     * @throws IllegalArgumentException if type or primary key is null,
     *      or primaryKey type does not correspond field of entity that is annotated with {@link com.bobocode.bibernate.annotation.Id}
     * @throws EntityMappingException if type does not have field that maps primary key column
     *      (annotated with {@link com.bobocode.bibernate.annotation.Id}),
     *      or type is not defined as entity (not annotated with {@link com.bobocode.bibernate.annotation.Entity})
     */
    <T> Optional<T> find(Class<T> type, Object primaryKey);

    /**
     * Find all entities by provided type. User should pass count of returned elements (limit parameter).
     * User can provide count of rows that should be skipped (offset parameter).
     * @param type specifies class of entity
     * @param limit defines count of rows that would be returned
     * @param offset defines count of rows that would be skipped (zero if none should be skipped)
     * @return list of found entities or empty list
     * @param <T> specifies type of entity
     *
     * @throws IllegalArgumentException if type is null, or limit is negative number, or offset is negative number
     *  {@link EntityMappingException} type is not defined as entity (not annotated with {@link com.bobocode.bibernate.annotation.Entity})
     */
    <T> List<T> findAll(Class<T> type, int limit, int offset);

    /**
     * Find all entities by provided column values.
     * @param type specifies class of entity
     * @param properties defines map of column name (key) and it's value for filtering
     * @return list of entities or empty list
     * @param <T> specifies type of entity
     *
     * @throws IllegalArgumentException if type or properties is null,
     *  {@link EntityMappingException} type is not defined as entity (not annotated with {@link com.bobocode.bibernate.annotation.Entity})
     */
    <T> List<T> findAll(Class<T> type, Map<String, Object> properties);

    <T> void save(T entity);
    <T> void update(T entity);
    <T> void delete(T entity);
    void close();

    void flush();
    void beginTransaction();
    void commitTransaction();
    void rollbackTransaction();

    Connection getConnection();
}
