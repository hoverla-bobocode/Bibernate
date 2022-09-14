package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.H2Dialect;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionImpl;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
class BaseH2Integration {

    protected Session session;

    private DataSource dataSource;

    @BeforeEach
    protected void setUp() throws SQLException {
        dataSource = createDataSource();
        session = initSession();
    }

    private DataSource createDataSource() throws SQLException {
        log.trace("Create data source");
        DataSource dataSource = new JdbcDataSource();
        dataSource.unwrap(JdbcDataSource.class)
                .setUrl("jdbc:h2:mem:default;INIT=RUNSCRIPT FROM 'src/test/resources/sql/product.sql'");
        return dataSource;
    }

    protected Session initSession() throws SQLException {
        log.trace("Create session");
        return new SessionImpl(dataSource, new H2Dialect());
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanup")) {
            return;
        }
        session.close();
    }
}
