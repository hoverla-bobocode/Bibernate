package com.bobocode.bibernate.action;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.Util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bobocode.bibernate.Dialect.*;

public class InsertAction extends AbstractAction {

    private final EntityPersister entityPersister;

    public InsertAction(EntityPersister entityPersister, Object entity) {
        super(entity);
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        Map<String, Object> columnsToInsert =
                Arrays.stream(entity.getClass().getDeclaredFields())
                        .collect(Collectors.toMap(Util::getColumnName, f -> Util.getValueFromField(f, entity)));

        List<Object> columnsValues = columnsToInsert.values().stream().toList();
        entityPersister.insert(prepareInsertQuery(entity, columnsToInsert.keySet()), columnsValues);
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
