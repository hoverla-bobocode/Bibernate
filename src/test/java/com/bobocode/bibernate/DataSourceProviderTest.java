package com.bobocode.bibernate;

import com.bobocode.bibernate.configuration.PersistenceUnitProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DataSourceProviderTest {

    private final static String DATA_SOURCE_JDBC_URL_PROPERTY = "persistenceUnit.dataSource.jdbcUrl";
    private final static String DATA_SOURCE_USER_PROPERTY = "persistenceUnit.dataSource.user";
    private final static String DATA_SOURCE_PASSWORD_PROPERTY = "persistenceUnit.dataSource.password";
    private final static String DIALECT_PROPERTY = "persistenceUnit.dialect";
    private final static String PERSISTENCE_UNIT_NAME_PROPERTY = "persistenceUnit.name";


    @Test
    void providePooledDatasource() {
        String url = "jdbc:h2:mem:testdb";
        String user = "sa";
        String password = "password";
        String driver = "org.h2.Driver";
        String dialect = "h2";
        String persistenceUnit = "h2";
        var properties = mockProperties(url, user, password, dialect, persistenceUnit);

        HikariDataSource dataSource =
                (HikariDataSource) PooledDataSourceProvider.providePooledDatasource(new PersistenceUnitProperties(properties));

        assertEquals(url, dataSource.getJdbcUrl());
        assertEquals(user, dataSource.getUsername());
        assertEquals(password, dataSource.getPassword());
        assertEquals(driver, dataSource.getDriverClassName());
    }

    private Map<String, String> mockProperties(String url, String user, String password, String dialect,
                                               String persistenceUnit) {
        Map<String, String> properties = new HashMap<>();

        properties.put(DATA_SOURCE_JDBC_URL_PROPERTY, url);
        properties.put(DATA_SOURCE_USER_PROPERTY, user);
        properties.put(DATA_SOURCE_PASSWORD_PROPERTY, password);
        properties.put(DIALECT_PROPERTY, dialect);
        properties.put(PERSISTENCE_UNIT_NAME_PROPERTY, persistenceUnit);
        return properties;
    }
}