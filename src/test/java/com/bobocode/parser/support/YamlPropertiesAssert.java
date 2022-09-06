package com.bobocode.parser.support;

import org.assertj.core.api.AbstractMapAssert;

import java.util.Map;
import java.util.Objects;

/**
 * Class to help assert test cases related to YamlPropertyParser
 */
public class YamlPropertiesAssert extends AbstractMapAssert<YamlPropertiesAssert, Map<String, String>, String, String> {

    private static final String PERSISTENCE_UNIT_NAME_PROPERTY = "persistenceUnit.name";
    private static final String LOG_LEVEL_PROPERTY = "logLevel";
    private static final String DATA_SOURCE_JDBC_URL_PROPERTY = "persistenceUnit.dataSource.jdbcUrl";
    private static final String DATA_SOURCE_USER_PROPERTY = "persistenceUnit.dataSource.user";
    private static final String DATA_SOURCE_PASSWORD_PROPERTY = "persistenceUnit.dataSource.password";
    private static final String DIALECT_PROPERTY = "persistenceUnit.dialect";

    public YamlPropertiesAssert(Map<String, String> actual) {
        super(actual, YamlPropertiesAssert.class);
    }

    public static YamlPropertiesAssert assertThat(Map<String, String> actual) {
        return new YamlPropertiesAssert(actual);
    }

    public YamlPropertiesAssert hasDataSourceProperties(String jdbcUrl, String user, String password) {
        return hasPropertyWithValue(DATA_SOURCE_JDBC_URL_PROPERTY, jdbcUrl)
                .hasPropertyWithValue(DATA_SOURCE_USER_PROPERTY, user)
                .hasPropertyWithValue(DATA_SOURCE_PASSWORD_PROPERTY, password);
    }

    public YamlPropertiesAssert hasUnitNameWithValue(String unitName) {
        return hasPropertyWithValue(PERSISTENCE_UNIT_NAME_PROPERTY, unitName);
    }

    public YamlPropertiesAssert hasLogLevelWithValue(String logLevel) {
        return hasPropertyWithValue(LOG_LEVEL_PROPERTY, logLevel);
    }

    public YamlPropertiesAssert hasDialectWithValue(String dialect) {
        return hasPropertyWithValue(DIALECT_PROPERTY, dialect);
    }

    private YamlPropertiesAssert hasPropertyWithValue(String property, String expectedValue) {
        isNotNull();

        if (!actual.containsKey(property)) {
            failWithMessage("Expected the map to contain key '%s' but it didn't", property);
        }

        final String actualValue = actual.get(property);
        if (!Objects.equals(expectedValue, actualValue)) {
            failWithMessage("Expected the map to have value '%s' for key '%s' but was '%s'", expectedValue, property, actualValue);
        }

        return this;
    }

    public YamlPropertiesAssert doesNotHaveLogLevel() {
        isNotNull();

        if (actual.containsKey(LOG_LEVEL_PROPERTY)) {
            failWithMessage("Expected the map not to contain key '%s' but it did", LOG_LEVEL_PROPERTY);
        }

        return this;
    }
}
