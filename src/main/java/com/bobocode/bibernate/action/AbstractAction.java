package com.bobocode.bibernate.action;

/**
 * Encapsulates common state between all {@link Action} implementations
 */
public abstract class AbstractAction implements Action {
    protected final Object entity;

    protected AbstractAction(Object entity) {
        this.entity = entity;
    }
}
