package com.bobocode.bibernate.session;

import com.bobocode.bibernate.action.Action;
import com.bobocode.bibernate.exception.EntityMappingException;

import com.bobocode.bibernate.transaction.Transaction;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Session is used to interact with persistence context. This interface is API for creating and removing persistence entity
 * instances, finding them by primary key or other properties.
 */
public interface Session extends AutoCloseable {

    /**
     * Find entity by primary key. Searching entity of passed type and primary key. If the entity instance is contained in the
     * persistence context, it is returned from there.
     * @param type       specifies class of entity
     * @param primaryKey value of primary key for filtering results
     * @param <T>        specifies type of entity
     * @return found entity optional
     * @throws IllegalArgumentException if type or primary key is null, or primaryKey type does not correspond field of entity that
     *                                  is annotated with {@link com.bobocode.bibernate.annotation.Id}
     * @throws EntityMappingException   if type does not have field that maps primary key column (annotated with
     *                                  {@link com.bobocode.bibernate.annotation.Id}), or type is not defined as entity (not
     *                                  annotated with {@link com.bobocode.bibernate.annotation.Entity})
     */
    <T> Optional<T> find(Class<T> type, Object primaryKey);

    /**
     * Find all entities by provided type. User should pass count of returned elements (limit parameter). User can provide count of
     * rows that should be skipped (offset parameter).
     * @param type   specifies class of entity
     * @param limit  defines count of rows that would be returned
     * @param offset defines count of rows that would be skipped (zero if none should be skipped)
     * @param <T>    specifies type of entity
     * @return list of found entities or empty list
     * @throws IllegalArgumentException if type is null, or limit is negative number, or offset is negative number
     *                                  {@link EntityMappingException} type is not defined as entity (not annotated with
     *                                  {@link com.bobocode.bibernate.annotation.Entity})
     */
    <T> List<T> findAll(Class<T> type, int limit, int offset);

    /**
     * Find all entities by provided column values.
     * @param type       specifies class of entity
     * @param properties defines map of column name (key) and it's value for filtering
     * @param <T>        specifies type of entity
     * @return list of entities or empty list
     * @throws IllegalArgumentException if type or properties is null, {@link EntityMappingException} type is not defined as entity
     *                                  (not annotated with {@link com.bobocode.bibernate.annotation.Entity})
     */
    <T> List<T> findAll(Class<T> type, Map<String, Object> properties);

    /**
     * Save entity in the database
     * @param entity the instance to be saved
     * @param <T>    specifies type of the entity
     * @throws IllegalStateException if session is closed {@link EntityMappingException} if entity is invalid
     */
    <T> void save(T entity);

    /**
     * Remove an entity from the database
     * @param entity the instance to be removed
     * @param <T>    specifies type of entity
     */
    <T> void delete(T entity);

    /**
     * Copies the state of the provided object to the . If three is no entity with the same id in the persistence context it will be
     * loaded as well. If provided entity has differences with entity from the context, the second one will be updated by first. If
     * there is no entity with the same id saved in db exception will be thrown.
     * @param <T> - entity to be added to the persistence context
     * @return merged entity
     */
    <T> T merge(T entity);

    /**
     * Removes provided entity from the persistence context cache
     * @param <T> - entity to be removed from the persistence context
     */
    <T> void detach(T entity);

    /**
     * Check if the entity connects  on this Session
     * @param entity an instance of a persistence class
     * @param <T>    specifies type of entity
     * @return true if the entity is connected with this Session
     */
    <T> boolean contains(T entity);

    /**
     * Starts resource transaction using {@link Transaction#begin()}
     */
    void begin();

    /**
     * Commits resource transaction using {@link Transaction#commit()}
     */
    void commit();

    /**
     * Force the transaction to roll back using {@link Transaction#rollback()}
     */
    void rollback();

    /**
     * Explicitly executes accumulated SQL commands
     */
    void flush();

    /**
     * Flushes all queued {@link Action`s} and closes {@link Connection}
     */
    void close();
}
