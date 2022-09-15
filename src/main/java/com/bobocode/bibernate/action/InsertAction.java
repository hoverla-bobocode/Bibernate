package com.bobocode.bibernate.action;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.PersistenceContext;
import com.bobocode.bibernate.Util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

import static com.bobocode.bibernate.configuration.Dialect.INSERT_TEMPLATE;
import static com.bobocode.bibernate.configuration.Dialect.PLACEHOLDER;
import static com.bobocode.bibernate.configuration.Dialect.prepareValuesClause;

/**
 * Represents INSERT SQL statement that will be lazily executed on a provided entity.
 */
@Slf4j
public class InsertAction extends AbstractAction {

    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;

    /**
     * @param entityPersister class that handles actual entity persistence
     * @param context used to save entity in {@link PersistenceContext persistence context}
     * @param entity table record represented as object to be inserted
     */
    public InsertAction(EntityPersister entityPersister, PersistenceContext context, Object entity) {
        super(entity);
        this.entityPersister = entityPersister;
        this.persistenceContext = context;
    }

    /**
     * Inserts given entity into DB and puts it in {@link PersistenceContext persistence context}.
     */
    @Override
    public void execute() {
        String tableName = Util.getTableName(entity.getClass());
        log.trace("Executing insert for entity '{}'", tableName);
        Field[] declaredFields = entity.getClass().getDeclaredFields();
        Set<String> columnNamesToInsert = getColumnNamesToInsert(declaredFields);
        String query = prepareInsertQuery(entity, columnNamesToInsert);
        List<Object> columnsValues = getColumnsValues(declaredFields);
        entityPersister.insert(query, columnsValues);
        log.trace("Entity '{}' is inserted into DB", tableName);
        persistenceContext.putEntity(entity, Util.getIdFieldValue(entity));
        log.trace("Entity '{}' is saved in Persistence Context", tableName);
    }

    private List<Object> getColumnsValues(Field[] declaredFields) {
        return Arrays.stream(declaredFields)
                .sorted(Comparator.comparing(Field::getName))
                .map(f -> Util.getValueFromField(f, entity))
                .toList();
    }

    private static Set<String> getColumnNamesToInsert(Field[] declaredFields) {
        return Arrays.stream(declaredFields)
                .map(Util::getColumnName)
                .collect(Collectors.toSet());
    }

    private String prepareInsertQuery(Object entity, Set<String> columnsToInsert) {
        String tableName = Util.getTableName(entity.getClass());
        String valuesClause = prepareValuesClause(columnsToInsert);
        String placeholders = Stream.generate(() -> PLACEHOLDER).limit(columnsToInsert.size()).collect(Collectors.joining(", "));
        return INSERT_TEMPLATE.formatted(tableName, valuesClause, placeholders);
    }


    @Override
    public ActionPriority getPriority() {
        return ActionPriority.INSERT_PRIORITY;
    }
}
