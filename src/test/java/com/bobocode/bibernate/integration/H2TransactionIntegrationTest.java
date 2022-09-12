package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.H2Dialect;
import com.bobocode.bibernate.integration.entity.Product;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Disabled
class H2TransactionIntegrationTest {

    private Session session;

    @BeforeEach
    void setUp() throws SQLException {
        session = initSession();
    }

    private Session initSession() throws SQLException {
        DataSource dataSource = createDataSource();
        return createSession(dataSource);
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
    @DisplayName("Entity is updated after commit transaction")
    void commit() {
        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(2.0);

        session.beginTransaction();
        Product product = session.find(Product.class, 1L).orElseThrow();
        product.price(2.0);
        session.flush();
        session.commitTransaction();
        session.detach(product);

        Product foundProduct = session.find(Product.class, 1L).orElseThrow();
        assertThat(foundProduct).isEqualTo(expectedProduct);
    }

    @Test
    @DisplayName("Entity is not updated after rollback transaction")
    void rollback() {
        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(1.0);

        session.beginTransaction();
        Product product = session.find(Product.class, 1L).orElseThrow();
        product.price(2.0);
        session.flush();
        session.rollbackTransaction();
        session.detach(product);

        Product foundProduct = session.find(Product.class, 1L).orElseThrow();
        assertThat(foundProduct)
                .isEqualTo(expectedProduct)
                .isNotEqualTo(product);
    }

    @Test
    @DisplayName("Entity is not updated when transaction is not committed")
    void notFinishTransaction() throws SQLException {
        session.beginTransaction();
        Product product = session.find(Product.class, 1L).orElseThrow();
        product.price(2.0);
        session.flush();
        session.close();

        session = initSession();
        Product sameProductFromDB = session.find(Product.class, 1L).orElseThrow();
        assertThat(product).isNotEqualTo(sameProductFromDB);
    }
}
