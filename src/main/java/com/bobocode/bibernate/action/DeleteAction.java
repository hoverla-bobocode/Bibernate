package com.bobocode.bibernate.action;

import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.PersistenceContext;
import com.bobocode.bibernate.Util;
import lombok.extern.slf4j.Slf4j;

import static com.bobocode.bibernate.configuration.Dialect.DELETE_QUERY;

/**
 * Represents DELETE SQL statement that will be lazily executed on a provided entity.
 */
@Slf4j
public class DeleteAction extends AbstractAction {
    private final EntityPersister entityPersister;
    private final PersistenceContext context;

    /**
     * @param entityPersister class that handles actual entity persistence
     * @param context used to remove entity from {@link PersistenceContext persistence context}
     * @param entity table record represented as object to be deleted
     */
    public DeleteAction(EntityPersister entityPersister, PersistenceContext context, Object entity) {
        super(entity);
        this.entityPersister = entityPersister;
        this.context = context;
    }

    /**
     * Deletes given entity from DB and removes it from {@link PersistenceContext persistence context}.
     */
    @Override
    public void execute() {
        Class<?> entityType = entity.getClass();
        String tableName = Util.getTableName(entityType);
        Object idFieldValue = Util.getIdFieldValue(entity);
        log.trace("Executing delete for entity '{}' #{}", tableName, idFieldValue);
        String deleteQuery = DELETE_QUERY.formatted(tableName);
        entityPersister.delete(deleteQuery, idFieldValue);
        log.trace("Entity '{}' #{} is deleted from DB", tableName, idFieldValue);
        context.evict(entity, idFieldValue);
        log.trace("Entity '{}' #{} is removed form Persistence Context", tableName, idFieldValue);
    }

    @Override
    public ActionPriority getPriority() {
        return ActionPriority.DELETE_PRIORITY;
    }
}
