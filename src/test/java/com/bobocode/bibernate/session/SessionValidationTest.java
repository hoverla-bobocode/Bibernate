package com.bobocode.bibernate.session;

import com.bobocode.bibernate.configuration.Dialect;
import com.bobocode.bibernate.exception.EntityMappingException;
import com.bobocode.bibernate.session.entity.EntityClass;
import com.bobocode.bibernate.session.entity.NotDefinedIdField;
import com.bobocode.bibernate.session.entity.NotEntityClass;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SessionValidationTest {

    @Mock
    private Dialect dialect;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private SessionImpl session;

    @Test
    @DisplayName("Throws NullPointerException when entity type is null")
    void throwsIllegalArgumentExceptionWhenEntityTypeIsNull() {
        List<ThrowableAssert.ThrowingCallable> findMethods = List.of(() -> session.find(null, 1L),
                () -> session.findAll(null, 1, 0),
                () -> session.findAll(null, Map.of("key", "value")));

        findMethods.forEach(method -> assertThatThrownBy(method)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("[type] argument must be not null"));
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when limit is negative")
    void throwsIllegalArgumentExceptionWhenLimitIsNegative() {
        assertThatThrownBy(() -> session.findAll(EntityClass.class, -1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[limit] argument cannot be negative number");
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when offset is negative")
    void throwsIllegalArgumentExceptionWhenOffsetIsNegative() {
        assertThatThrownBy(() -> session.findAll(EntityClass.class, 2, -3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[offset] argument cannot be negative number");
    }

    @Test
    @DisplayName("Throws NullPointerException when properties map is null")
    void throwsIllegalArgumentExceptionWhenPropertiesMapIsNull() {
        assertThatThrownBy(() -> session.findAll(EntityClass.class, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("[properties] argument must be not null");
    }

    @Test
    @DisplayName("Throws NullPointerException when primary key is null")
    void throwsIllegalArgumentExceptionWhenPrimaryKeyIsNull() {
        assertThatThrownBy(() -> session.find(EntityClass.class, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("[primaryKey] argument must be not null");
    }

    @Test
    @DisplayName("Throws EntityMappingException when class is not defined as entity")
    void throwsEntityMappingExceptionWhenClassIsNotEntity() {
        List<ThrowableAssert.ThrowingCallable> findMethods = List.of(() -> session.find(NotEntityClass.class, 1L),
                () -> session.findAll(NotEntityClass.class, 1, 0),
                () -> session.findAll(NotEntityClass.class, Map.of("key", "value")));

        findMethods.forEach(method -> assertThatThrownBy(method)
                .isInstanceOf(EntityMappingException.class)
                .hasMessage(NotEntityClass.class.getName() + " is not defined as entity"));
    }

    @Test
    @DisplayName("Throws EntityMappingException when class does not have field annotated with @Id")
    void throwsEntityMappingExceptionWhenNoIdField() {
        long id = 1L;
        assertThatThrownBy(() -> session.find(NotDefinedIdField.class, id))
                .isInstanceOf(EntityMappingException.class)
                .hasMessage("Entity class must have field annotated with @Id");
    }
}
