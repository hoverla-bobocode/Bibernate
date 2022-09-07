package com.bobocode.bibernate.exception;

/**
 * This is general type of Bibernate exceptions
 */
public class BibernateException extends RuntimeException {
    public BibernateException(String message) {
        super(message);
    }

    public BibernateException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
