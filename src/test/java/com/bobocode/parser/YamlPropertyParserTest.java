package com.bobocode.parser;

import com.bobocode.exception.PersistenceFileNotFoundException;
import com.bobocode.exception.PersistenceUnitNotFoundException;
import com.bobocode.support.YamlPropertiesAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class YamlPropertyParserTest {

    @Test
    @DisplayName("Read properties default file")
    void testReadProperties() {
        YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();

        Map<String, String> properties = yamlPropertyParser.readPropertiesForPersistenceUnit("h2");

        YamlPropertiesAssert.assertThat(properties)
                .hasDataSourceProperties("jdbc:h2:mem:testdb", "sa", "password")
                .hasUnitNameWithValue("h2")
                .hasLogLevelWithValue("INFO")
                .hasDialectWithValue("h2");
    }

    @Test
    @DisplayName("Read properties default file throw PersistenceUnitNotFoundException when unit not found")
    void testReadPropertiesPersistenceUnitNotFound() {
        YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        String invalidPersistenceUnitName = "    ";

        assertThatThrownBy(() -> yamlPropertyParser.readPropertiesForPersistenceUnit(invalidPersistenceUnitName))
                .isInstanceOf(PersistenceUnitNotFoundException.class)
                .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
    }

    @Test
    @DisplayName("Read properties provide file")
    void testReadPropertiesForFile() {
        YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();

        Map<String, String> properties = yamlPropertyParser.readPropertiesForPersistenceUnit(
                "test-persistence.yml", "mysql");

        YamlPropertiesAssert.assertThat(properties)
                .hasUnitNameWithValue("mysql")
                .doesNotHaveLogLevel();
    }

    @Test
    @DisplayName("Read properties provide file throw PersistenceUnitNotFoundException when unit not found")
    void testReadPropertiesForFilePersistenceUnitNotFound() {
        YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        String invalidPersistenceUnitName = "    ";

        assertThatThrownBy(() -> yamlPropertyParser.readPropertiesForPersistenceUnit("test-persistence.yml", invalidPersistenceUnitName))
                .isInstanceOf(PersistenceUnitNotFoundException.class)
                .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
    }

    @Test
    @DisplayName("Read properties provide file throw PersistenceFileNotFoundException when file not found")
    void testReadPropertiesForFileThrowPersistenceFileNotFoundException() {
        YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        String invalidPersistenceUnitName = "    ";
        String invalidFileName = "invalid-file.yml";

        assertThatThrownBy(() -> yamlPropertyParser.readPropertiesForPersistenceUnit(invalidFileName, invalidPersistenceUnitName))
                .isInstanceOf(PersistenceFileNotFoundException.class)
                .hasMessage(String.format("Persistence file %s was not found", invalidFileName));
    }
}
