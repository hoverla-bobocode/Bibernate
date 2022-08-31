package com.bobocode.bibernate.session;

import com.bobocode.bibernate.Dialect;
import com.bobocode.bibernate.EntityPersister;
import com.bobocode.bibernate.Util;
import com.bobocode.bibernate.exception.BibernateException;
import com.bobocode.bibernate.session.entity.EntityClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionTest {
    @Mock
    private EntityPersister entityPersister;

    @Mock
    private Dialect dialect;

    @InjectMocks
    private SessionImpl session;

    @Test
    @DisplayName("Returns result when DB returns only 1 record by ID")
    void findById() {
        long primaryKey = 1;
        Class<EntityClass> type = EntityClass.class;
        String query = "select * from %s where id = ?".formatted(Util.getTableName(type));
        List<EntityClass> queryResult = Collections.singletonList(new EntityClass());
        when(entityPersister.select(type, query, List.of(primaryKey))).thenReturn(queryResult);

        Optional<EntityClass> entity = session.find(type, primaryKey);

        assertThat(entity).isPresent();
    }

    @Test
    @DisplayName("Throws exception when DB returns more than 1 record by ID")
    void findByIdHavingMoreThan1Result() {
        long primaryKey = 1;
        Class<EntityClass> type = EntityClass.class;
        String query = "select * from %s where id = ?".formatted(Util.getTableName(type));
        List<EntityClass> queryResult = List.of(new EntityClass(), new EntityClass());
        when(entityPersister.select(type, query, List.of(primaryKey))).thenReturn(queryResult);

        assertThatThrownBy(() -> session.find(EntityClass.class, primaryKey))
                .isInstanceOf(BibernateException.class)
                .hasMessage("More than 1 result were found!");
    }

    @Test
    @DisplayName("Returns empty result when DB returns 0 records by ID")
    void findByIdHavingEmptyResult() {
        long primaryKey = 1;
        Class<EntityClass> type = EntityClass.class;
        String query = "select * from %s where id = ?".formatted(Util.getTableName(type));
        List<EntityClass> queryResult = List.of();
        when(entityPersister.select(type, query, List.of(primaryKey))).thenReturn(queryResult);

        Optional<EntityClass> entity = session.find(EntityClass.class, primaryKey);

        assertThat(entity).isEmpty();
    }
}
