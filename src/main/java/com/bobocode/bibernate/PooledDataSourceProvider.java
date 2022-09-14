package com.bobocode.bibernate;

import com.bobocode.bibernate.configuration.DialectResolver;
import com.bobocode.bibernate.configuration.PersistenceUnitProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;

import javax.sql.DataSource;

/**
 * Provider provides pooled {@link HikariDataSource} based on provided properties of persistence-unit
 */
@UtilityClass
public class PooledDataSourceProvider {

    public DataSource providePooledDatasource(PersistenceUnitProperties properties) {
        final HikariConfig hikariConfig = new HikariConfig();
        final String driverClassName = DialectResolver.resolveDialect(properties.getDatabaseName()).getDriverClassName();

        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(properties.getJdbcUrl());
        hikariConfig.setUsername(properties.getUser());
        hikariConfig.setPassword(properties.getPassword());
        return new HikariDataSource(hikariConfig);
    }
}
