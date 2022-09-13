package com.bobocode.exception;

public class PersistenceFileNotFoundException extends RuntimeException {

    public PersistenceFileNotFoundException(String message) {
        super(message);
    }
}
