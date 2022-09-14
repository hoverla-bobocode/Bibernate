package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.integration.entity.Product;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactory;
import com.bobocode.bibernate.session.SessionFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.SQLException;
import java.util.Optional;

import static com.bobocode.bibernate.QueryHelper.runWithinTx;
import static com.bobocode.bibernate.QueryHelper.runWithinTxReturning;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QueryHelperIntegrationTest extends BaseH2Integration {
    @Mock
    private SessionFactory sessionFactory;

    // todo: remove this method later
    @Override
    @BeforeEach
    protected void setUp() throws SQLException {
        super.setUp();
        sessionFactory = mock(SessionFactoryImpl.class);
    }

    @Test
    // todo: remove this line later
    @Tag("SkipCleanup")
    void findById() {
        // todo: remove this line later
        when(sessionFactory.createSession()).thenReturn(session);

        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(1.0);

        Optional<Product> product = runWithinTxReturning(sessionFactory,
                (session) -> session.find(Product.class, 1L));
        assertThat(product).isPresent().contains(expectedProduct);
    }

    @Test
    // todo: remove this later
    @Tag("SkipCleanup")
    void updateData() throws SQLException {
        // todo: remove this line later (session factory should return new session instance)
        when(sessionFactory.createSession()).thenReturn(session);
        // todo: remove this later
        Session session1 = initSession();

        runWithinTx(sessionFactory,
                (session) -> {
                    Product foundProduct = session.find(Product.class, 1L).orElseThrow();
                    foundProduct.price(2.0);
                    session.flush();
                });


        // todo: remove this later
        when(sessionFactory.createSession()).thenReturn(session1);

        Optional<Product> updatedProduct = runWithinTxReturning(sessionFactory,
                (session_) -> session_.find(Product.class, 1L));

        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().price()).isEqualTo(2.0);

    }
}
