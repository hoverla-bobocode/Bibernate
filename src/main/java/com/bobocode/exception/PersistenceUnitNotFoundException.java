package com.bobocode.exception;

public class PersistenceUnitNotFoundException extends RuntimeException {

    public PersistenceUnitNotFoundException(String message) {
        super(message);
    }
}
