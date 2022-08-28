package com.bobocode.parser;

import com.bobocode.exception.PersistenceFileNotFoundException;
import com.bobocode.exception.PersistenceUnitNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class YamlPropertyParser implements PropertyParser {

    private static final String DEFAULT_PERSISTENCE_FILE = "persistence.yml";
    private static final String PERSISTENCE_UNIT_PROPERTY = "persistence-unit";
    private static final String NAME_PROPERTY = "name";

    @Override
    public Map<String, String> readPropertiesForPersistenceUnit(String persistenceUnit) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        log.info("Looking up for the file: {}", DEFAULT_PERSISTENCE_FILE);
        final InputStream persistencePropertiesFile = getPropertyFile(DEFAULT_PERSISTENCE_FILE, contextClassLoader);
        return getPropertiesForUnit(persistencePropertiesFile, persistenceUnit);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getPropertiesForUnit(InputStream propertyFile, String unitName) {
        final Yaml yaml = new Yaml();

        final Iterable<Object> objectIterable = yaml.loadAll(propertyFile);

        log.trace("Looking up for the persistence unit: {}", unitName);
        final Object propertyBlock = StreamSupport.stream(objectIterable.spliterator(), false)
                .filter(propertiesBlock -> hasCorrectPersistenceUnitName(unitName, propertiesBlock))
                .findAny()
                .orElseThrow(() -> {
                    final String errorMessage = String.format("Persistence unit %s was not found", unitName);
                    log.error(errorMessage);
                    return new PersistenceUnitNotFoundException(errorMessage);
                });
        log.trace("Persistence unit {} was found", unitName);

        log.trace("Resolving properties from the property block for the current persistence unit");
        final Object collectedProperties = mapObjectTo(propertyBlock, Map.class).entrySet().stream()
                .flatMap(propertyRoot -> getProperties(mapObjectTo(propertyRoot, Map.Entry.class)))
                .collect(Collectors.toMap(property -> mapObjectTo(property, Map.Entry.class).getKey(),
                        property -> mapObjectTo(property, Map.Entry.class).getValue()));

        return mapObjectTo(collectedProperties, Map.class);
    }

    private <T> T mapObjectTo(Object object, Class<T> mappingClass) {
        return mappingClass.cast(object);
    }

    private Stream<Map.Entry<String, String>> getProperties(Map.Entry<String, Object> propertyMap) {
        return getPropertyName(propertyMap.getKey(), propertyMap);
    }

    @SuppressWarnings("unchecked")
    private Stream<Map.Entry<String, String>> getPropertyName(
            String propertyNamePrefix, Map.Entry<String, ?> currentProperty) {
        if (Map.class.isAssignableFrom(currentProperty.getValue().getClass())) {
            return mapObjectTo(currentProperty.getValue(), Map.class).entrySet().stream()
                    .flatMap(property -> {
                        final Map.Entry<String, Object> innerProperty = mapObjectTo(property, Map.Entry.class);
                        return getPropertyName(
                                propertyNamePrefix + "." + innerProperty.getKey(), innerProperty);
                    });
        }
        final String propertyName = propertyNamePrefix.contains(currentProperty.getKey()) ? propertyNamePrefix :
                propertyNamePrefix + ".";
        final String propertyValue = mapObjectTo(currentProperty.getValue(), String.class);

        log.trace("Found property: {}={}", propertyName, propertyValue);
        return Stream.of(Map.entry(propertyName, propertyValue));
    }

    private boolean hasCorrectPersistenceUnitName(String persistenceUnitName, Object propertiesBlock) {
        if (mapObjectTo(propertiesBlock, Map.class).containsKey(PERSISTENCE_UNIT_PROPERTY)) {
            final Object persistenceUnit = mapObjectTo(propertiesBlock, Map.class).get(PERSISTENCE_UNIT_PROPERTY);
            if (mapObjectTo(persistenceUnit, Map.class).containsKey(NAME_PROPERTY)) {
                final Object currentPersistenceUnitName = mapObjectTo(persistenceUnit, Map.class).get(NAME_PROPERTY);
                return currentPersistenceUnitName.equals(persistenceUnitName);
            }
        }
        return false;
    }

    @Override
    public Map<String, String> readPropertiesForPersistenceUnit(String propertyFile, String persistenceUnit) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        log.info("Looking up for the file: {}", propertyFile);
        final InputStream persistencePropertiesFile = getPropertyFile(propertyFile, contextClassLoader);
        return getPropertiesForUnit(persistencePropertiesFile, persistenceUnit);
    }

    private InputStream getPropertyFile(String resourceName, ClassLoader contextClassLoader) {
        final InputStream propertyFile = contextClassLoader.getResourceAsStream(resourceName);
        if (propertyFile == null) {
            final String errorMessage = String.format("Persistence file %s was not found", resourceName);
            log.error(errorMessage);
            throw new PersistenceFileNotFoundException(errorMessage);
        }
        log.info("Persistence file {} was found", resourceName);

        return propertyFile;
    }
}
