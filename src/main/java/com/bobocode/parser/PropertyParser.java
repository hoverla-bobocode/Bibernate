package com.bobocode.parser;

import java.util.Map;

/**
 * Parses a .yml file into a {@link Map Map<String, String>}, where key of type {@link String} represents <b>property name</b>,
 * and value of type {@link String} represents <b>property value</b>
 * A file may contain multiple blocks separated by the <b>---</b> sign (3 dashes). Provide a block name with the <b>persistence-unit.name</b> property
 * If no file name was provided, the default file named <b>persistence.yml</b> will be looked up
 */
public interface PropertyParser {

    Map<String, String> readPropertiesForPersistenceUnit(String persistenceUnit);

    Map<String, String> readPropertiesForPersistenceUnit(String propertyFile, String persistenceUnit);
}
