package com.bobocode.bibernate.action;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.Util;

import static com.bobocode.bibernate.Dialect.*;


public class DeleteAction extends AbstractAction {
    private final EntityPersister entityPersister;

    public DeleteAction(EntityPersister entityPersister, Object entity) {
        super(entity);
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        Class<?> entityType = entity.getClass();
        String tableName = Util.getTableName(entityType);
        Object idFieldValue = Util.getIdFieldValue(entity);
        String deleteQuery = DELETE_QUERY.formatted(tableName);
        entityPersister.delete(deleteQuery, idFieldValue);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.DELETE_PRIORITY;
    }
}
