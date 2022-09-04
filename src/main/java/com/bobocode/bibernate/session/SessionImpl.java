package com.bobocode.bibernate.session;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.PersistenceContext;
import com.bobocode.bibernate.Util;
import com.bobocode.bibernate.Validator;
import com.bobocode.bibernate.action.Action;
import com.bobocode.bibernate.action.DeleteAction;
import com.bobocode.bibernate.action.InsertAction;
import com.bobocode.bibernate.action.UpdateAction;
import com.bobocode.bibernate.configuration.Dialect;
import com.bobocode.bibernate.exception.BibernateException;
import com.bobocode.bibernate.transaction.Transaction;
import com.bobocode.bibernate.transaction.TransactionImpl;
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

import static com.bobocode.bibernate.Util.getTableName;
import static com.bobocode.bibernate.Util.mergeEntities;
import static com.bobocode.bibernate.configuration.Dialect.SELECT_ALL_BY_PROPERTIES_TEMPLATE;
import static com.bobocode.bibernate.configuration.Dialect.SELECT_ALL_ID_TEMPLATE;
import static com.bobocode.bibernate.configuration.Dialect.SELECT_ALL_TEMPLATE;
import static com.bobocode.bibernate.configuration.Dialect.prepareWhereClause;

@Slf4j
public class SessionImpl implements Session {

    private static final String TYPE_MUST_NOT_BE_NULL_MSG = "[type] argument must be not null";

    private final Dialect dialect;

    private final EntityPersister entityPersister;

    private final PersistenceContext persistenceContext;

    private final Queue<Action> actionQueue;

    private Transaction transaction;

    private final Connection connection;

    private boolean isOpen;

    public SessionImpl(DataSource dataSource, Dialect dialect) throws SQLException {
        this.connection = dataSource.getConnection();
        this.dialect = dialect;
        this.entityPersister = new EntityPersister(connection);
        this.persistenceContext = new PersistenceContext();
        this.actionQueue = new PriorityQueue<>(Action.comparingPriority());
        this.isOpen = true;
    }

    @Override
    public <T> Optional<T> find(Class<T> type, Object primaryKey) {
        checkIsOpen();
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
        T entity = foundEntities.get(0);
        persistenceContext.putEntity(entity, primaryKey);
        persistenceContext.putEntitySnapshot(entity, primaryKey);
        return Optional.of(entity);
    }

    @Override
    public <T> List<T> findAll(Class<T> type, int limit, int offset) {
        checkIsOpen();
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
        checkIsOpen();
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
        checkIsOpen();
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());
        actionQueue.offer(new InsertAction(entityPersister, persistenceContext, entity));
    }

    private <T> void update(T entity, Map<String, Object> updatedColumns) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(updatedColumns);
        Validator.validateEntity(entity.getClass());
        actionQueue.offer(new UpdateAction(entityPersister, entity, updatedColumns));
    }

    @Override
    public <T> void delete(T entity) {
        checkIsOpen();
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());
        Object cachedEntity = persistenceContext.getEntity(entity.getClass(), Util.getIdFieldValue(entity))
                .orElseThrow(() -> new BibernateException("Detached entity cannot be removed"));
        actionQueue.offer(new DeleteAction(entityPersister, persistenceContext, cachedEntity));
    }

    @Override
    public <T> T merge(T entity) {
        checkIsOpen();
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());

        Class<?> entityType = entity.getClass();
        Object idValue = Util.getIdFieldValue(entity);

        Optional<?> cachedEntity = persistenceContext.getEntity(entityType, idValue);
        if (cachedEntity.isPresent()) {
            return (T) mergeEntities(entity, cachedEntity.get());
        }

        Object loadedEntity = find(entityType, idValue).orElseThrow();
        Optional<?> cachedLoadedEntity = persistenceContext.getEntity(loadedEntity.getClass(), Util.getIdFieldValue(loadedEntity));
        return (T) mergeEntities(entity, cachedLoadedEntity.orElseThrow());
    }

    @Override
    public <T> void detach(T entity) {
        checkIsOpen();
        Objects.requireNonNull(entity);
        Validator.validateEntity(entity.getClass());

        Object idFieldValue = Util.getIdFieldValue(entity);
        persistenceContext.evict(entity, idFieldValue);
    }

    @Override
    public <T> boolean contains(T entity) {
        checkIsOpen();
        Objects.requireNonNull(entity);
        return persistenceContext.getEntity(entity.getClass(), Util.getIdFieldValue(entity)).isPresent();
    }

    @Override
    public void flush() {
        checkIsOpen();
        log.trace("Flushing session queued actions");
        var updatedEntitiesColumnsMap = persistenceContext.getUpdatedEntitiesColumnsMap();
        updatedEntitiesColumnsMap.forEach(this::update);
        while (!actionQueue.isEmpty()) {
            actionQueue.poll().execute();
        }
    }

    @Override
    public void begin() {
        checkIsOpen();
        initTransaction();
        transaction.begin();
    }

    @Override
    public void commit() {
        checkIsOpen();
        checkTransactionIsInitialized();
        flush();
        transaction.commit();
    }

    @Override
    public void rollback() {
        checkIsOpen();
        checkTransactionIsInitialized();
        transaction.rollback();
    }

    private void initTransaction() {
        if (transaction == null) {
            transaction = new TransactionImpl(connection);
        }
    }

    private void checkTransactionIsInitialized() {
        if (transaction == null) {
            throw new BibernateException("Transaction was not initialized");
        }
    }

    @Override
    public void close() {
        checkIsOpen();
        log.trace("Closing session");
        flush();
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Failed to close session");
            throw new BibernateException("Failed to close session", e);
        }
        this.isOpen = false;
    }

    private void checkIsOpen() {
        if (!isOpen) {
            throw new IllegalStateException("Session is already closed");
        }
    }
}
