package com.bobocode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.bobocode.bibernate.Dialect.prepareWhereClause;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DialectTest {

    @Test
    @DisplayName("Property map converts to string")
    void propertiesMapConvertsToStringForWhereClause() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("string", "String");
        properties.put("integer", 1);
        properties.put("big_decimal", BigDecimal.valueOf(5.02));
        properties.put("timestamp", "2012-02-15");

        String expectedResult = "big_decimal = ? AND integer = ? AND string = ? AND timestamp = ?";

        assertEquals(expectedResult, prepareWhereClause(properties.keySet()));
    }
}
