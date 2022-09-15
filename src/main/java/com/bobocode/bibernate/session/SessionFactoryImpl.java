package com.bobocode.bibernate.session;

import com.bobocode.bibernate.PooledDataSourceProvider;
import com.bobocode.bibernate.configuration.Dialect;
import com.bobocode.bibernate.configuration.PersistenceUnitProperties;
import com.bobocode.bibernate.exception.BibernateException;
import com.bobocode.parser.PropertyParser;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import static com.bobocode.parser.YamlPropertyParser.toPersistenceUnitProperties;

@Slf4j
public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;
    private final Dialect dialect;
    private final PersistenceUnitProperties properties;

    public SessionFactoryImpl(PropertyParser parser, String persistenceUnit) {
        Objects.requireNonNull(parser, "Property parser must not be null");
        this.properties = getPersistenceUnitProperties(parser, persistenceUnit, null);
        this.dataSource = getDataSource();
        this.dialect = properties.getDialect();
    }

    public SessionFactoryImpl(PropertyParser parser, String persistenceUnit, String propertyFile) {
        Objects.requireNonNull(parser, "Property parser must not be null");
        Objects.requireNonNull(propertyFile, "Property file name must not be null");
        this.properties = getPersistenceUnitProperties(parser, persistenceUnit, propertyFile);
        this.dataSource = getDataSource();
        this.dialect = properties.getDialect();
    }

    public Session openSession() {
        try {
            log.info("Creating session...");
            return new SessionImpl(dataSource, dialect);
        } catch (SQLException e) {
            throw new BibernateException("Connection problem: %s", e);
        }
    }

    @Override
    public void close() {
        final HikariDataSource unwrap;
        try {
            unwrap = dataSource.unwrap(HikariDataSource.class);
            unwrap.close();
            log.info("Session closed.");
        } catch (SQLException e) {
            throw new BibernateException("Error during closing connections. ", e);
        }
    }

    private DataSource getDataSource() {
        return PooledDataSourceProvider.providePooledDatasource(properties);
    }

    private PersistenceUnitProperties getPersistenceUnitProperties(PropertyParser parser, String persistenceUnit,
            String propertyFile) {
        return toPersistenceUnitProperties(Optional.ofNullable(propertyFile)
                .map(file -> parser.readPropertiesForPersistenceUnit(file, persistenceUnit))
                .orElseGet(() -> parser.readPropertiesForPersistenceUnit(persistenceUnit)));
    }
}
