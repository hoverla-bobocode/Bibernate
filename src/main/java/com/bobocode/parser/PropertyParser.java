package com.bobocode.parser;

import java.util.Map;

/**
 * Parses a .yml file into a {@link Map Map<String, String>}, where key of type {@link String} represents <b>property name</b>,
 * and value of type {@link String} represents <b>property value</b> <p>
 * If no file name was provided, the default file named <b>persistence.yml</b> will be looked up <p>
 * A file may contain multiple blocks separated by the <b>---</b> sign (3 dashes). Usage of multiple blocks in one file <b>is optional</b>
 * <p>
 * Example:
 * <pre>
 * logLevel: INFO
 *
 * persistenceUnit:
 *   name: h2
 *   dataSource:
 *     jdbcUrl: jdbc:h2:mem:testdb
 *     user: sa
 *     password: password
 *   dialect: h2
 *
 * </pre>
 * <p>
 * Example with multiple blocks:
 * <pre>
 * logLevel: INFO
 *
 * persistenceUnit:
 *   name: h2
 *   dataSource:
 *     jdbcUrl: jdbc:h2:mem:testdb
 *     user: sa
 *     password: password
 *   dialect: h2
 *
 * ---
 * persistenceUnit:
 *   name: unused-unit
 *   dataSource:
 *     jdbcUrl: unused-url
 *     user: root
 *     password: root
 * </pre>
 */
public interface PropertyParser {

    /**
     * Read properties from the <b>persistence.yml</b> file. File should be inside resources
     *
     * @param persistenceUnit - persistence unit name, specified in the <code>persistenceUnit.name</code> property
     * @return {@link Map Map<String, String>} that has property name as key and property value as map's value
     */
    Map<String, String> readPropertiesForPersistenceUnit(String persistenceUnit);

    /**
     * Read properties from the file. File should be inside resources
     *
     * @param propertyFile    - resource name
     * @param persistenceUnit - persistence unit name, specified in the <code>persistenceUnit.name</code> property
     * @return {@link Map Map<String, String>} that has property name as key and property value as map's value
     */
    Map<String, String> readPropertiesForPersistenceUnit(String propertyFile, String persistenceUnit);
}
