package com.bobocode.bibernate.exception;

/**
 * Throws to indicate exception that happened in the DB
 */
public class BibernateSQLException extends RuntimeException {
    public BibernateSQLException(String message, Throwable e) {
        super(message, e);
    }
}
