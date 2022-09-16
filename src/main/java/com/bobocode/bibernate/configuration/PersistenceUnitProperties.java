package com.bobocode.bibernate.configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.bobocode.bibernate.exception.BibernateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static com.bobocode.bibernate.configuration.PropertyValues.DATA_SOURCE_JDBC_URL_PROPERTY;
import static com.bobocode.bibernate.configuration.PropertyValues.DATA_SOURCE_PASSWORD_PROPERTY;
import static com.bobocode.bibernate.configuration.PropertyValues.DATA_SOURCE_USER_PROPERTY;
import static com.bobocode.bibernate.configuration.PropertyValues.DIALECT_PROPERTY;
import static com.bobocode.bibernate.configuration.PropertyValues.LOG_LEVEL_PROPERTY;
import static com.bobocode.bibernate.configuration.PropertyValues.PERSISTENCE_UNIT_NAME_PROPERTY;

/**
 * PersistenceUnitProperties store properties for Bibernate.
 */
@Getter
@Slf4j
public class PersistenceUnitProperties {

    private String persistenceUnitName;
    private String databaseName;
    private Dialect dialect;
    private String jdbcUrl;
    private String user;
    private String password;
    private Level logLevel;

    /**
     * Reads and sets properties such as: persistenceUnitName, jdbcUrl, userName and password which are base for connection to
     * database; if dialect name is not provided then resolve it from connection metadata; if logLevel is not provided then sets it
     * Level.DEBUG as default
     * @param properties            map of properties
     * @throws BibernateException   if some required property is invalid
     */
    public PersistenceUnitProperties(Map<String, String> properties) {
        readPersistenceUnitName(properties);
        readDataSourceMetaData(properties);
        readDatabaseNameFromConnectionMetaData();
        readDialect(properties);
        readLogLevel(properties);
        setLogLevelForLogger();
    }

    private void setLogLevelForLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        logger.setLevel(logLevel);
    }

    private void readLogLevel(Map<String, String> properties) {
        final String propertyLevel = properties.get(LOG_LEVEL_PROPERTY.value);
        logLevel = Level.toLevel(propertyLevel, Level.DEBUG);
    }

    private void readPersistenceUnitName(Map<String, String> properties) {
        persistenceUnitName = Optional.of(properties.get(PERSISTENCE_UNIT_NAME_PROPERTY.value))
                .orElseThrow(() -> new BibernateException("Persistence name was not provided in properties file"));
    }

    private void readDialect(Map<String, String> properties) {
        final String dialectFromProperties = properties.get(DIALECT_PROPERTY.value);
        if (dialectFromProperties != null) {
            matchingDialectNames(dialectFromProperties);
        }
        dialect = DialectResolver.resolveDialect(databaseName).getInstance();
    }

    private void readDatabaseNameFromConnectionMetaData() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            databaseName = metaData.getDatabaseProductName();
        } catch (SQLException sqlException) {
            log.error(sqlException.getMessage());
            throw new BibernateException(sqlException.getMessage());
        }
    }

    private void readDataSourceMetaData(Map<String, String> properties) {
        jdbcUrl = Optional.of(properties.get(DATA_SOURCE_JDBC_URL_PROPERTY.value))
                .orElseThrow(() -> new BibernateException("Url was not provided in properties file"));
        user = Optional.of(properties.get(DATA_SOURCE_USER_PROPERTY.value))
                .orElseThrow(() -> new BibernateException("User was not provided in properties file"));
        password = Optional.of(properties.get(DATA_SOURCE_PASSWORD_PROPERTY.value))
                .orElseThrow(() -> new BibernateException("Password was not provided in properties file"));
    }

    private void matchingDialectNames(String dialectFromProperties) {
        if (!databaseName.equalsIgnoreCase(dialectFromProperties)) {
            throw new BibernateException("The specified dialect does not match the dialect of the driver");
        }
    }
}

