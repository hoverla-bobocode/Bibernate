package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.H2Dialect;
import com.bobocode.bibernate.integration.entity.Product;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class H2TransactionIntegrationTest {

    private Session session;

    @BeforeEach
    void setUp() throws SQLException {
        DataSource dataSource = createDataSource();
        session = createSession(dataSource);
    }

    private static DataSource createDataSource() throws SQLException {
        DataSource dataSource = new JdbcDataSource();
        dataSource.unwrap(JdbcDataSource.class)
                .setUrl("jdbc:h2:mem:default;INIT=RUNSCRIPT FROM 'src/test/resources/sql/product.sql'");
        return dataSource;
    }

    private static SessionImpl createSession(DataSource dataSource) {
        return new SessionImpl(dataSource, new H2Dialect());
    }

    @Test
    @DisplayName("Gets record by ID")
    void getRecordById() {
        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(1.0);

        session.beginTransaction();
        Product product = session.find(Product.class, 1L).orElseThrow();
        product.price(1.0);
        session.update(product); //unsupported

        Product sameProductFromDB = session.find(Product.class, 1L).orElseThrow();
        assertThat(product).isNotEqualTo(sameProductFromDB);

    }
}
