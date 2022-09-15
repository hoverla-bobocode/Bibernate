package com.bobocode.bibernate.configuration;

import com.bobocode.bibernate.configuration.dialects.H2Dialect;
import com.bobocode.bibernate.configuration.dialects.PostgreSQLDialect;

/**
 * Enum holds an instance of the {@link SqlDialect} corresponding by dialect name
 */
public enum SqlDialect {
    POSTGRESQL(new PostgreSQLDialect(), "org.postgresql.Driver"),

    H2(new H2Dialect(), "org.h2.Driver"),

    MYSQL(null, "com.mysql.jdbc.Driver");

    private final Dialect instance;
    private final String driverClassName;

    SqlDialect(Dialect instance, String driverClassName) {
        this.instance = instance;
        this.driverClassName = driverClassName;
    }

    /**
     * Retrieves an instance of corresponding implementation of {@link Dialect}
     * @return {@link Dialect} corresponding to enums name
     */
    public Dialect getInstance() {
        return instance;
    }

    /**
     * Retrieves class name of corresponding sql driver
     * @return {@link String} - name of driver class
     */
    public String getDriverClassName() {
        return driverClassName;
    }
}
