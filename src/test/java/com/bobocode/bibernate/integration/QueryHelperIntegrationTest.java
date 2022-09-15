package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.exception.QueryHelperException;
import com.bobocode.bibernate.integration.entity.Product;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.bobocode.bibernate.QueryHelper.runWithinTx;
import static com.bobocode.bibernate.QueryHelper.runWithinTxReturning;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryHelperIntegrationTest extends BaseH2Integration {

    @Test
    void findById() {
        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(1.0);

        Optional<Product> product = runWithinTxReturning(sessionFactory,
                (session) -> session.find(Product.class, 1L));
        assertThat(product).isPresent().contains(expectedProduct);
    }

    @Test
    void updateData() {
        runWithinTx(sessionFactory,
                (session) -> {
                    Product foundProduct = session.find(Product.class, 1L).orElseThrow();
                    foundProduct.price(2.0);
                    session.flush();
                });


        Optional<Product> updatedProduct = runWithinTxReturning(sessionFactory,
                (session) -> session.find(Product.class, 1L));

        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().price()).isEqualTo(2.0);

    }

    @Test
    void throwsQueryHelperException() {
        assertThatThrownBy(() -> runWithinTxReturning(sessionFactory,
                (session) -> session.find(String.class, 1L)))
                .isInstanceOf(QueryHelperException.class);
    }

}
