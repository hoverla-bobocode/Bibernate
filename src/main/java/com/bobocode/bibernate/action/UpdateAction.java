package com.bobocode.bibernate.action;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.Util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

import static com.bobocode.bibernate.configuration.Dialect.UPDATE_TEMPLATE;
import static com.bobocode.bibernate.configuration.Dialect.prepareSetClause;
import static com.bobocode.bibernate.configuration.Dialect.prepareWhereClause;

/**
 * Represents UPDATE SQL statement that will be lazily executed on a provided entity.
 */
@Slf4j
public class UpdateAction extends AbstractAction {

    private final EntityPersister entityPersister;
    private final Map<String, Object> updatedColumns;

    /**
     * @param entityPersister class that handles actual entity persistence
     * @param entity table record represented as object to be inserted
     * @param updatedColumns column name to value map that represents table columns that are needed to be used in UPDATE query
     */
    public UpdateAction(EntityPersister entityPersister, Object entity, Map<String, Object> updatedColumns) {
        super(entity);
        this.entityPersister = entityPersister;
        this.updatedColumns = updatedColumns;
    }

    /**
     * Updates given {@link UpdateAction#updatedColumns entity's columns} in DB.
     */
    @Override
    public void execute() {
        String tableName = Util.getTableName(entity.getClass());
        Object idValue = Util.getIdFieldValue(entity);
        log.trace("Executing update for entity '{}' #{}", tableName, idValue);
        String query = prepareUpdateQuery(entity, updatedColumns);
        List<Object> sortedColumnValues = getSortedColumnValues();
        List<Object> propertiesToFilter = List.of(Util.getIdFieldValue(entity));
        entityPersister.update(query, sortedColumnValues, propertiesToFilter);
        log.trace("Entity '{}' is updated in DB", tableName);
    }

    private List<Object> getSortedColumnValues() {
        return updatedColumns
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }

    private String prepareUpdateQuery(Object entity, Map<String, Object> updatedColumns) {
        String tableName = Util.getTableName(entity.getClass());

        Field idField = Util.getIdField(entity.getClass());
        String idColumnName = Util.getColumnName(idField);
        String setClause = prepareSetClause(updatedColumns.keySet());
        String whereClause = prepareWhereClause(Set.of(idColumnName));
        return UPDATE_TEMPLATE.formatted(tableName, setClause, whereClause);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.UPDATE_PRIORITY;
    }
}
