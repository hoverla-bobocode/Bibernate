package com.bobocode.bibernate.session;

import com.bobocode.bibernate.Dialect;
import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.PersistenceContext;
import com.bobocode.bibernate.Util;
import com.bobocode.bibernate.Validator;
import com.bobocode.bibernate.Transaction;
import com.bobocode.bibernate.action.Action;
import com.bobocode.bibernate.action.DeleteAction;
import com.bobocode.bibernate.action.InsertAction;
import com.bobocode.bibernate.action.UpdateAction;
import com.bobocode.bibernate.exception.BibernateException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.bobocode.bibernate.Dialect.SELECT_ALL_BY_PROPERTIES_TEMPLATE;
import static com.bobocode.bibernate.Dialect.SELECT_ALL_ID_TEMPLATE;
import static com.bobocode.bibernate.Dialect.SELECT_ALL_TEMPLATE;
import static com.bobocode.bibernate.Dialect.prepareWhereClause;
import static com.bobocode.bibernate.Util.getTableName;

@Slf4j
public class SessionImpl implements Session {

    private static final String TYPE_MUST_NOT_BE_NULL_MSG = "[type] argument must be not null";

    private final Dialect dialect;

    private final EntityPersister entityPersister;

    private final PersistenceContext persistenceContext;

    private final Queue<Action> actionQueue;
    private final Connection connection;

    public SessionImpl(DataSource dataSource, Dialect dialect) throws SQLException {
        this.dialect = dialect;
        this.connection = dataSource.getConnection();
        this.entityPersister = new EntityPersister(connection);
        this.persistenceContext = new PersistenceContext();
        this.actionQueue = new PriorityQueue<>(Action.comparingPriority());
    }

    @Override
    public <T> Optional<T> find(Class<T> type, Object primaryKey) {
        Objects.requireNonNull(type, TYPE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(primaryKey, "[primaryKey] argument must be not null");

        Optional<T> cachedEntity = persistenceContext.getEntity(type, primaryKey);
        if (cachedEntity.isPresent()) {
            return cachedEntity;
        }

        Validator.validateEntity(type);
        Validator.checkIdValidPrimaryKeyType(type, primaryKey);

        String tableName = getTableName(type);
        log.trace("Finding {} by id", tableName);

        String query = SELECT_ALL_ID_TEMPLATE.formatted(tableName);
        List<T> foundEntities = entityPersister.select(type, query, List.of(primaryKey));
        if (foundEntities.isEmpty()) {
            return Optional.empty();
        }
        if (foundEntities.size() != 1) {
            throw new BibernateException("More than 1 result were found!");
        }
        Optional<T> entity = Optional.of(foundEntities.get(0));
        persistenceContext.putEntity(entity.get(), primaryKey);
        persistenceContext.putEntitySnapshot(primaryKey, entity.get());
        return entity;
    }

    @Override
    public <T> List<T> findAll(Class<T> type, int limit, int offset) {
        Objects.requireNonNull(type, TYPE_MUST_NOT_BE_NULL_MSG);
        Validator.checkNotNegativeNumber(limit, "[limit] argument cannot be negative number");
        Validator.checkNotNegativeNumber(offset, "[offset] argument cannot be negative number");
        Validator.validateEntity(type);

        String tableName = getTableName(type);
        log.trace("Finding all {}", tableName);

        String query = SELECT_ALL_TEMPLATE.formatted(tableName) + dialect.getLimitClause(limit, offset);
        List<Object> properties = offset != 0 ? List.of(limit, offset) : List.of(limit);

        return entityPersister.select(type, query, properties);
    }

    @Override
    public <T> List<T> findAll(Class<T> type, Map<String, Object> properties) {
        Objects.requireNonNull(type, TYPE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(properties, "[properties] argument must be not null");
        Validator.validateEntity(type);

        String tableName = getTableName(type);
        log.trace("Finding {} by properties", tableName);

        String query = SELECT_ALL_BY_PROPERTIES_TEMPLATE.formatted(tableName, prepareWhereClause(properties.keySet()));

        List<Object> values = properties.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();

        return entityPersister.select(type, query, values);
    }

    @Override
    public <T> void save(T entity) {
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());
        actionQueue.offer(new InsertAction(entityPersister, entity));
    }

    public <T> void update(T entity, Map<String, Object> updatedColumns) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(updatedColumns);
        Validator.validateEntity(entity.getClass());
        actionQueue.offer(new UpdateAction(entityPersister, entity, updatedColumns));
    }

    @Override
    public <T> void delete(T entity) {
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());
        actionQueue.offer(new DeleteAction(entityPersister, entity));
    }

    @Override
    public <T> T merge(T entity) {
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());

        Class<?> entityType = entity.getClass();
        Object idValue = Util.getIdFieldValue(entity);

        return persistenceContext.getEntity(entityType, idValue)
                .map(o -> (T) o)
                .orElseGet(() -> (T) find(entityType, idValue).orElseThrow());
    }

    @Override
    public <T> void detach(T entity) {
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());

        Object idFieldValue = Util.getIdFieldValue(entity);
        persistenceContext.evict(entity, idFieldValue);
    }

    @Override
    public void flush() {
        log.trace("Flushing session queued actions");
        var updatedEntitiesColumnsMap = persistenceContext.getUpdatedEntitiesColumnsMap();
        updatedEntitiesColumnsMap.forEach(this::update);
        actionQueue.forEach(Action::execute);
    }

    @Override
    public Transaction beginTransaction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Transaction commitTransaction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Transaction rollbackTransaction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        log.trace("Closing session");
        flush();
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Failed to close session");
            throw new BibernateException(e);
        }
    }
}
