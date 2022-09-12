package com.bobocode.bibernate.action;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.Util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bobocode.bibernate.Dialect.UPDATE_TEMPLATE;
import static com.bobocode.bibernate.Dialect.prepareSetClause;
import static com.bobocode.bibernate.Dialect.prepareWhereClause;

public class UpdateAction extends AbstractAction {

    private final EntityPersister entityPersister;
    private final Map<String, Object> updatedColumns;
    public UpdateAction(EntityPersister entityPersister, Object entity, Map<String, Object> updatedColumns) {
        super(entity);
        this.entityPersister = entityPersister;
        this.updatedColumns = updatedColumns;
    }

    @Override
    public void execute() {
        String query = prepareUpdateQuery(entity, updatedColumns);
        List<Object> sortedColumnValues = updatedColumns
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
        List<Object> propertiesToFilter = List.of(Util.getIdFieldValue(entity));
        entityPersister.update(query, sortedColumnValues, propertiesToFilter);
    }

    private String prepareUpdateQuery(Object entity, Map<String, Object> updatedColumns) {
        Field idField = Util.getIdField(entity.getClass());
        String idColumnName = Util.getColumnName(idField);
        String tableName = Util.getTableName(entity.getClass());
        String setClause = prepareSetClause(updatedColumns.keySet());
        String whereClause = prepareWhereClause(Set.of(idColumnName));
        return UPDATE_TEMPLATE.formatted(tableName, setClause, whereClause);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.UPDATE_PRIORITY;
    }
}
