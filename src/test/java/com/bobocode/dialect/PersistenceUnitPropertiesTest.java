package com.bobocode.dialect;

import com.bobocode.bibernate.configuration.PersistenceUnitProperties;
import com.bobocode.parser.YamlPropertyParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceUnitPropertiesTest {
    private static final String H2 = "H2";
    public static final String JDBC_URL = "jdbc:h2:mem:testdb";
    public static final String SA = "sa";
    public static final String PASSWORD = "password";
    public static final String LOG_INFO = "INFO";

    private static Stream<Arguments> invalidProperties() {
        var parser = new YamlPropertyParser();
        return Stream.of(
                Arguments.of(parser.readPropertiesForPersistenceUnit("unsupported-dialect"), "No suitable driver found for unsupported-url"),
                Arguments.of(parser.readPropertiesForPersistenceUnit("h2-with-incorrect-dialect"), "The specified dialect does not match the dialect of the driver")
        );
    }

    private static Stream<Arguments> validProperties() {
        var parser = new YamlPropertyParser();
        return Stream.of(
                Arguments.of(parser.readPropertiesForPersistenceUnit("h2"), "h2"),
                Arguments.of(parser.readPropertiesForPersistenceUnit("h2-unprovided-dialect"), "h2-unprovided-dialect")
        );
    }

    @ParameterizedTest(name = "Throws an exception [{1}]")
    @MethodSource("invalidProperties")
    void negativeTest(Map<String, String> map, String exceptionMessage) {
        Assertions.assertThatThrownBy(
                        () -> new PersistenceUnitProperties(map))
                .hasMessage(exceptionMessage);
    }

    @ParameterizedTest(name = "Read properties for persistence unit [{1}] and create PersistenceUnitProperties")
    @MethodSource("validProperties")
    void positiveTest(Map<String, String> map, String persistenceUnitName) {
        var persistenceUnitProperties = new PersistenceUnitProperties(map);
        assertionPersistenceUnitProperties(persistenceUnitProperties);
    }

    private void assertionPersistenceUnitProperties(PersistenceUnitProperties properties) {
        assertEquals(JDBC_URL, properties.getJdbcUrl());
        assertEquals(SA, properties.getUser());
        assertEquals(PASSWORD, properties.getPassword());
        assertEquals(H2, properties.getDatabaseName());
        assertEquals(LOG_INFO, properties.getLogLevel().toString());
    }
}
