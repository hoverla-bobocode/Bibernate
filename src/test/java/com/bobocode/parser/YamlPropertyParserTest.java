package com.bobocode.parser;

import com.bobocode.exception.PersistenceFileNotFoundException;
import com.bobocode.exception.PersistenceUnitNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlPropertyParserTest {

    private static final String PERSISTENCE_UNIT_NAME_PROPERTY = "persistence-unit.name";
    private static final String LOG_LEVEL_PROPERTY = "log-level";
    private static final String DATA_SOURCE_JDBC_URL_PROPERTY = "persistence-unit.dataSource.jdbcUrl";
    private static final String DATA_SOURCE_USER_PROPERTY = "persistence-unit.dataSource.user";
    private static final String DATA_SOURCE_PASSWORD_PROPERTY = "persistence-unit.dataSource.password";
    private static final String DIALECT_PROPERTY = "persistence-unit.dialect";

    @Test
    void testReadPropertiesForPersistenceUnit_DefaultFileName() {
        // Given
        final YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();

        // When
        final Map<String, String> properties = yamlPropertyParser.readPropertiesForPersistenceUnit("h2");

        // Then
        assertContainsDataSourceProperties(properties, "jdbc:h2:mem:testdb", "sa", "password");

        assertTrue(properties.containsKey(PERSISTENCE_UNIT_NAME_PROPERTY));
        assertEquals("h2", properties.get(PERSISTENCE_UNIT_NAME_PROPERTY));

        assertTrue(properties.containsKey(LOG_LEVEL_PROPERTY));
        assertEquals("INFO", properties.get(LOG_LEVEL_PROPERTY));

        assertTrue(properties.containsKey(DIALECT_PROPERTY));
        assertEquals("h2", properties.get(DIALECT_PROPERTY));
    }

    @Test
    void testReadPropertiesForPersistenceUnit_DefaultFileName_UnitNotFound_ThrowPersistenceUnitNotFoundException() {
        // Given
        final YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        final String invalidPersistenceUnitName = "    ";

        // When
        // Then
        assertThatThrownBy(() -> yamlPropertyParser.readPropertiesForPersistenceUnit(invalidPersistenceUnitName))
                .isInstanceOf(PersistenceUnitNotFoundException.class)
                .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
    }

    @Test
    void testReadPropertiesForPersistenceUnit_CustomFileName() {
        // Given
        final YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();

        // When
        final Map<String, String> properties = yamlPropertyParser.readPropertiesForPersistenceUnit(
                "test-persistence.yml", "mysql");

        // Then
        assertContainsDataSourceProperties(properties,
                "jdbc:mysql://127.0.0.1:3306/test_database", "root", "root");

        assertTrue(properties.containsKey(PERSISTENCE_UNIT_NAME_PROPERTY));
        assertEquals("mysql", properties.get(PERSISTENCE_UNIT_NAME_PROPERTY));

        assertFalse(properties.containsKey(LOG_LEVEL_PROPERTY));
        assertNull(properties.get(LOG_LEVEL_PROPERTY));
    }

    private void assertContainsDataSourceProperties(Map<String, String> properties,
                                                    String jdbcUrl, String user, String password) {
        assertTrue(properties.containsKey(DATA_SOURCE_JDBC_URL_PROPERTY));
        assertEquals(jdbcUrl, properties.get(DATA_SOURCE_JDBC_URL_PROPERTY));

        assertTrue(properties.containsKey(DATA_SOURCE_USER_PROPERTY));
        assertEquals(user, properties.get(DATA_SOURCE_USER_PROPERTY));

        assertTrue(properties.containsKey(DATA_SOURCE_PASSWORD_PROPERTY));
        assertEquals(password, properties.get(DATA_SOURCE_PASSWORD_PROPERTY));
    }

    @Test
    void testReadPropertiesForPersistenceUnit_CustomFileName_UnitNotFound_ThrowPersistenceUnitNotFoundException() {
        // Given
        final YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        final String invalidPersistenceUnitName = "    ";

        // When
        // Then
        assertThatThrownBy(() -> yamlPropertyParser.readPropertiesForPersistenceUnit("test-persistence.yml", invalidPersistenceUnitName))
                .isInstanceOf(PersistenceUnitNotFoundException.class)
                .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
    }

    @Test
    void testReadPropertiesForPersistenceUnit_CustomFileName_FileNotFound_ThrowPersistenceFileNotFoundException() {
        // Given
        final YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        final String invalidPersistenceUnitName = "    ";
        final String invalidFileName = "invalid-file.yml";

        // When
        // Then
        assertThatThrownBy(() -> yamlPropertyParser.readPropertiesForPersistenceUnit(invalidFileName, invalidPersistenceUnitName))
                .isInstanceOf(PersistenceFileNotFoundException.class)
                .hasMessage(String.format("Persistence file %s was not found", invalidFileName));
    }
}
