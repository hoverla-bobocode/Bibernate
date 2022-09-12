package com.bobocode.bibernate;

import com.bobocode.bibernate.integration.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextTest {

    private PersistenceContext persistenceContext;

    @BeforeEach
    void setUp() {
        persistenceContext = new PersistenceContext();
    }

    @Test
    void getEntityWithNonExistingKey() {
        Optional<Product> actual = persistenceContext.getEntity(Product.class, 1000L);
        assertThat(actual).isEmpty();
    }

    @Test
    void putEntity() {
        Product entity = new Product();
        persistenceContext.putEntity(entity, 1L);

        Optional<Product> actual = persistenceContext.getEntity(Product.class, 1L);

        assertThat(actual).hasValue(entity);
    }

    @Test
    void getUpdatedEntitiesColumnsMap() {
        long id = 1L;
        Product entity = new Product().id(id);
        Product entitySnapshot = new Product().id(id);
        persistenceContext.putEntity(entity, id);
        persistenceContext.putEntitySnapshot(id, entitySnapshot);

        String newName = "new Name";
        double newPrice = 10.0;
        entity.name(newName).price(newPrice);

        var updatedEntitiesColumnsMap = persistenceContext.getUpdatedEntitiesColumnsMap();

        assertThat(updatedEntitiesColumnsMap)
                .containsEntry(entity, Map.of("name", newName, "price", newPrice));
    }
}