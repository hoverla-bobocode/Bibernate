package com.bobocode.bibernate.action;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.PersistenceContext;
import com.bobocode.bibernate.Util;

import static com.bobocode.bibernate.configuration.Dialect.DELETE_QUERY;


public class DeleteAction extends AbstractAction {
    private final EntityPersister entityPersister;
    private final PersistenceContext context;

    public DeleteAction(EntityPersister entityPersister, PersistenceContext context, Object entity) {
        super(entity);
        this.entityPersister = entityPersister;
        this.context = context;
    }

    @Override
    public void execute() {
        Class<?> entityType = entity.getClass();
        String tableName = Util.getTableName(entityType);
        Object idFieldValue = Util.getIdFieldValue(entity);
        String deleteQuery = DELETE_QUERY.formatted(tableName);
        entityPersister.delete(deleteQuery, idFieldValue);
        context.evict(entity, idFieldValue);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.DELETE_PRIORITY;
    }
}
