package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactoryImpl;
import com.bobocode.parser.YamlPropertyParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

@Slf4j
class BaseH2Integration {

    protected SessionFactoryImpl sessionFactory;
    protected Session session;
    protected String persistenceUnitName;

    @BeforeEach
    void init() {
        YamlPropertyParser parser = new YamlPropertyParser();
        sessionFactory = new SessionFactoryImpl(parser, persistenceUnitName == null ? "h2-integration-test" : persistenceUnitName);
        session = sessionFactory.openSession();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanup")) {
            return;
        }
        session.close();
        sessionFactory.close();
    }
}
