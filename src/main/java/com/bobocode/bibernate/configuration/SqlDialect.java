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

    public Dialect getInstance() {
        return instance;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

}
