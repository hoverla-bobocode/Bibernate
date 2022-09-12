package com.bobocode.bibernate.action;

public abstract class AbstractAction implements Action {
    protected final Object entity;

    protected AbstractAction(Object entity) {
        this.entity = entity;
    }
}
