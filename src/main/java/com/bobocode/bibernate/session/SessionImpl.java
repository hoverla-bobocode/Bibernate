package com.bobocode.bibernate.session;

import com.bobocode.bibernate.Dialect;
import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.PersistenceContext;
import com.bobocode.bibernate.Util;
import com.bobocode.bibernate.Validator;
import com.bobocode.bibernate.Transaction;
import com.bobocode.bibernate.exception.BibernateException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.bobocode.bibernate.Dialect.SELECT_ALL_BY_PROPERTIES_TEMPLATE;
import static com.bobocode.bibernate.Dialect.SELECT_ALL_ID_TEMPLATE;
import static com.bobocode.bibernate.Dialect.SELECT_ALL_TEMPLATE;
import static com.bobocode.bibernate.Dialect.UPDATE_TEMPLATE;
import static com.bobocode.bibernate.Dialect.prepareSetClause;
import static com.bobocode.bibernate.Dialect.prepareWhereClause;
import static com.bobocode.bibernate.Util.getTableName;

@Slf4j
public class SessionImpl implements Session {

    private static final String TYPE_MUST_NOT_BE_NULL_MSG = "[type] argument must be not null";

    private final Dialect dialect;

    private final EntityPersister entityPersister;

    private final PersistenceContext persistenceContext;

    public SessionImpl(DataSource dataSource, Dialect dialect) {
        this.dialect = dialect;
        this.entityPersister = new EntityPersister(dataSource);
        this.persistenceContext = new PersistenceContext();
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
        persistenceContext.putEntity(primaryKey, entity.get());
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
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void update(T entity) {
        throw new UnsupportedOperationException();
    }

    private <T> void update(T entity, Map<String, Object> updatedColumns) {
        String query = prepareUpdateQuery(entity, updatedColumns);
        List<Object> sortedColumnValues = updatedColumns
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
        List<Object> propertiesToFilter = List.of(Util.getValueFromField(Util.getIdField(entity.getClass()), entity));
        entityPersister.update(query, sortedColumnValues, propertiesToFilter);
    }

    @Override
    public <T> void delete(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        var updatedEntitiesColumnsMap = persistenceContext.getUpdatedEntitiesColumnsMap();
        updatedEntitiesColumnsMap.forEach(this::update);
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
       flush();
    }

    private String prepareUpdateQuery(Object entity, Map<String, Object> updatedColumns) {
        Field idField = Util.getIdField(entity.getClass());
        String idColumnName = Util.getColumnName(idField);
        String tableName = Util.getTableName(entity.getClass());
        String setClause = prepareSetClause(updatedColumns.keySet());
        String whereClause = prepareWhereClause(Set.of(idColumnName));
        return UPDATE_TEMPLATE.formatted(tableName, setClause, whereClause);
    }
}
