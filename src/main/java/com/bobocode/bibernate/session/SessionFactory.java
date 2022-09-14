package com.bobocode.bibernate.session;


/**
 * SessionFactory uses for creation new {@link Session} instances based on provided SQL dialect and close exist sessions.
 */
public interface SessionFactory {

    Session openSession();

    void close();

}
