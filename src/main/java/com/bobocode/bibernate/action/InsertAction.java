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

import static com.bobocode.bibernate.configuration.Dialect.INSERT_TEMPLATE;
import static com.bobocode.bibernate.configuration.Dialect.PLACEHOLDER;
import static com.bobocode.bibernate.configuration.Dialect.prepareValuesClause;

public class InsertAction extends AbstractAction {

    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;

    public InsertAction(EntityPersister entityPersister, PersistenceContext persistenceContext, Object entity) {
        super(entity);
        this.entityPersister = entityPersister;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public void execute() {
        Field[] declaredFields = entity.getClass().getDeclaredFields();
        Set<String> columnNamesToInsert = getColumnNamesToInsert(declaredFields);
        String query = prepareInsertQuery(entity, columnNamesToInsert);
        List<Object> columnsValues = getColumnsValues(declaredFields);
        entityPersister.insert(query, columnsValues);
        persistenceContext.putEntity(entity, Util.getIdFieldValue(entity));
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
