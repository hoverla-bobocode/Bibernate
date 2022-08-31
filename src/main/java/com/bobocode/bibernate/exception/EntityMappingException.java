package com.bobocode.bibernate.exception;

/**
 * Thrown to indicate that class is not an entity
 */
public class EntityMappingException extends RuntimeException {
    public EntityMappingException(String message) {
        super(message);
    }
    public EntityMappingException(String message, Throwable e) {
        super(message, e);
    }
}
