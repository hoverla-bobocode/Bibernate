package com.bobocode.bibernate;

import com.bobocode.bibernate.session.SessionFactory;
import com.bobocode.bibernate.session.SessionFactoryImpl;
import com.bobocode.parser.YamlPropertyParser;
import lombok.experimental.UtilityClass;

/**
 * Starting point to create {@link SessionFactory session factory}
 */
@UtilityClass
public class Persistence {

    /**
     * @param persistenceUnitName persistence unit name from which configuration properties should be taken. By default,
     * searches for persistence.yml.
     */
    public SessionFactory createSessionFactory(String persistenceUnitName) {
        YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        return new SessionFactoryImpl(yamlPropertyParser, persistenceUnitName);
    }

    /**
     * @param persistenceFileName persistence file name from which configuration properties should be taken.
     * @param persistenceUnitName persistence unit name from which configuration properties should be taken.
     */
    public SessionFactory createSessionFactory(String persistenceFileName, String persistenceUnitName) {
        YamlPropertyParser yamlPropertyParser = new YamlPropertyParser();
        return new SessionFactoryImpl(yamlPropertyParser, persistenceUnitName, persistenceFileName);
    }
}
