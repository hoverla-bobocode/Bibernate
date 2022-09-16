package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.integration.entity.Person;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactory;
import com.bobocode.bibernate.session.SessionFactoryImpl;
import com.bobocode.parser.PropertyParser;
import com.bobocode.parser.YamlPropertyParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SessionFactoryImplTest {

    private final PropertyParser propertyParser = new YamlPropertyParser();

    private SessionFactory sessionFactory;

    @AfterEach
    void tearDown() {
        sessionFactory.close();
    }

    @Test
    @SneakyThrows
    void openSession() {
        Person expectedPerson = new Person();
        expectedPerson.id(2L).name("Bilbo").age(129);

        sessionFactory = new SessionFactoryImpl(propertyParser, "h2", "session-factory-test-persistence.yml");

        Optional<Person> person;
        try (Session session = sessionFactory.openSession()) {
            person = session.find(Person.class, 2L);
        }

        assertThat(person).isPresent().contains(expectedPerson);
    }
}