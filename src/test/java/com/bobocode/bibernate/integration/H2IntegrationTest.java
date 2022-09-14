package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.integration.entity.Product;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactoryImpl;
import com.bobocode.bibernate.session.entity.EntityClass;
import com.bobocode.bibernate.session.entity.NotEntityClass;
import com.bobocode.parser.YamlPropertyParser;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class H2IntegrationTest extends BaseH2Integration {

    @Test
    @DisplayName("Gets record by ID")
    void getRecordById() {
        Product expectedProduct = new Product();
        expectedProduct.id(3L).name("knife").price(5.0);

        Optional<Product> product = session.find(Product.class, 3L);

        assertThat(product).isPresent().contains(expectedProduct);
    }

    @Test
    @DisplayName("Gets cached record by ID")
    void getCachedRecordById() {
        Product expectedProduct = new Product();
        expectedProduct.id(1L).name("scissors").price(1.0);

        Optional<Product> product = session.find(Product.class, 1L);
        Optional<Product> cachedProduct = session.find(Product.class, 1L);

        assertThat(product).containsSame(cachedProduct.orElseThrow());
    }

    @Test
    @DisplayName("Gets empty record by non-existing ID")
    void getEmptyRecordById() {
        Optional<Product> product = session.find(Product.class, 1000L);
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

    @Test
    @DisplayName("Calls update on entity which fields were actually updated during the session")
    void callsUpdateOnUpdatedEntity() {
        Optional<Product> product = session.find(Product.class, 3L);
        Product updatableProduct = product.orElseThrow();
        String newProductName = "new product name";
        updatableProduct.name(newProductName);

        session.flush();

        Optional<Product> updatedProduct = session.find(Product.class, updatableProduct.id());
        assertThat(updatedProduct)
                .isPresent()
                .map(Product::name)
                .hasValue(newProductName);
    }

    @Test
    void delete() {
        Optional<Product> product = session.find(Product.class, 2L);
        Product productToDelete = product.orElseThrow();

        session.delete(productToDelete);

        boolean remainsInContext = session.contains(productToDelete);
        assertThat(remainsInContext).isTrue();

        session.flush();
        remainsInContext = session.contains(productToDelete);
        assertThat(remainsInContext).isFalse();
    }

    @Test
    void merge() {
        Product foundProduct = session.find(Product.class, 1L).orElseThrow();
        session.detach(foundProduct);
        String newProductName = "new scissors name";
        foundProduct.name(newProductName);
        assertThat(session.contains(foundProduct)).isFalse();
        Product managedProduct = session.merge(foundProduct);
        assertThat(session.contains(managedProduct)).isTrue();
        assertThat(managedProduct).isNotSameAs(foundProduct);
        assertThat(managedProduct.name()).isEqualTo(newProductName);
    }

    @Test
    void detach() {
        Product foundProduct = session.find(Product.class, 2L).orElseThrow();
        assertThat(session.contains(foundProduct)).isTrue();
        session.detach(foundProduct);
        assertThat(session.contains(foundProduct)).isFalse();
    }

    @Test
    void save() {
        Product product = new Product().id(100L).name("product name");

        boolean cached = session.contains(product);
        assertThat(cached).isFalse();

        session.save(product);
        session.flush();

        cached = session.contains(product);
        assertThat(cached).isTrue();
    }

    @Test
    void updateOperations() {
        Product foundProduct = session.find(Product.class, 1L).orElseThrow();
        session.delete(foundProduct);
        Product newProduct = new Product().id(200L).name("product name");
        foundProduct.price(21.2);
        session.save(newProduct);
        session.flush();

        assertThat(session.contains(foundProduct)).isFalse();
        assertThat(session.contains(newProduct)).isTrue();
    }

    @Test
    @Tag("SkipCleanup")
    @DisplayName("Throws IllegalStateException when session is closed")
    void throwsIllegalStateExceptionWhenSessionIsClosed() {
        session.close();
        EntityClass entity = new EntityClass();

        List<ThrowableAssert.ThrowingCallable> allMethods = List.of(
                () -> session.find(NotEntityClass.class, 3L),
                () -> session.findAll(NotEntityClass.class, 3, 0),
                () -> session.findAll(NotEntityClass.class, Map.of("key", "value")),
                () -> session.contains(entity),
                () -> session.merge(entity),
                () -> session.save(entity),
                () -> session.delete(entity),
                () -> session.detach(entity),
                () -> session.flush(),
                () -> session.begin(),
                () -> session.commit(),
                () -> session.rollback(),
                () -> session.close()
        );

        allMethods.forEach(method -> assertThatThrownBy(method)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Session is already closed"));
    }
}
