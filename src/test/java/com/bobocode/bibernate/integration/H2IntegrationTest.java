package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.EntityPersister;
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

class H2IntegrationTest {
    private Session session;

    @BeforeEach
    void setUp() throws SQLException {
        DataSource dataSource = new JdbcDataSource();
        dataSource.unwrap(JdbcDataSource.class)
                .setUrl("jdbc:h2:mem:default;INIT=RUNSCRIPT FROM 'src/test/resources/sql/product.sql'");

        session = new SessionImpl(new EntityPersister(dataSource), new H2Dialect());
    }

    @Test
    @DisplayName("Gets record by ID")
    void getRecordById() {
        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(1.0);

        Optional<Product> product = session.find(Product.class, 1L);

        assertThat(product).isPresent().contains(expectedProduct);
    }

    @Test
    @DisplayName("Gets empty record by non-existing ID")
    void getEmptyRecordById() {
        Optional<Product> product = session.find(Product.class, 10L);
        assertThat(product).isEmpty();
    }


    @Test
    @DisplayName("Gets record by ID and properties")
    void getAllRecordsByIdAndProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "scissors");
        List<Product> expectedProducts = List.of(
                new Product().id(1L).name("scissors").price(1.0)
        );

        List<Product> products = session.findAll(Product.class, properties);

        assertThat(products).containsAll(expectedProducts);
    }

    @Test
    @DisplayName("Gets all records bounded by limit and offset")
    void getAllRecordsByIdWithLimitAndOffset() {
        List<Product> expectedProducts = List.of(
                new Product().id(1L).name("scissors").price(1.0),
                new Product().id(2L).name("rope").price(10.0)
        );

        List<Product> limitedProducts = session.findAll(Product.class, 2, 0);

        assertThat(limitedProducts).containsAll(expectedProducts);
    }
}