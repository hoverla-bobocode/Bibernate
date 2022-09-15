package com.bobocode.bibernate.configuration;

/**
 * Class that holds property names to read properties from resource files.
 */
public enum PropertyValues {
    PERSISTENCE_UNIT_NAME_PROPERTY("persistenceUnit.name"),
    DATA_SOURCE_JDBC_URL_PROPERTY("persistenceUnit.dataSource.jdbcUrl"),
    DATA_SOURCE_USER_PROPERTY("persistenceUnit.dataSource.user"),
    DATA_SOURCE_PASSWORD_PROPERTY("persistenceUnit.dataSource.password"),
    DIALECT_PROPERTY("persistenceUnit.dialect"),
    LOG_LEVEL_PROPERTY("logLevel");

    public final String value;

    PropertyValues(java.lang.String propertyName) {
        this.value = propertyName;
    }
}
