package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.integration.entity.Product;
import com.bobocode.bibernate.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class H2TransactionIntegrationTest extends BaseH2Integration {

    @Test
    @DisplayName("Entity is updated after commit transaction")
    void commit() {
        Double newPrice = 2.0;

        session.begin();
        Product product = session.find(Product.class, 1L).orElseThrow();
        product.price(newPrice);
        session.flush();
        session.commit();
        session.detach(product);

        Product foundProduct = session.find(Product.class, 1L).orElseThrow();
        assertThat(foundProduct.price()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("Entity is not updated after rollback transaction")
    void rollback() {
        Double oldPrice = 1.0;
        Double newPrice = 2.0;
        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(1.0);

        session.begin();
        Product product = session.find(Product.class, 1L).orElseThrow();
        product.price(newPrice);
        session.flush();
        session.rollback();
        session.detach(product);

        Product foundProduct = session.find(Product.class, 1L).orElseThrow();
        assertThat(foundProduct.price())
                .isEqualTo(oldPrice)
                .isNotEqualTo(newPrice);
    }

    @Test
    @Tag("SkipCleanup")
    @DisplayName("Entity is not updated when transaction is not committed")
    void notFinishTransaction() throws SQLException {
        Session session1 = initSession();
        Double newPrice = 2.0;

        session.begin();
        Product product = session.find(Product.class, 1L).orElseThrow();
        product.price(newPrice);
        session.flush();
        session.close();

        Product sameProductFromDB = session1.find(Product.class, 1L).orElseThrow();
        assertThat(product.price()).isNotEqualTo(sameProductFromDB.price());

        session1.close();
    }
}
