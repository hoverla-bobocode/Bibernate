package com.bobocode.bibernate.integration;

import com.bobocode.bibernate.integration.entity.Person;
import com.bobocode.bibernate.integration.entity.Product;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactoryImpl;
import com.bobocode.parser.YamlPropertyParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class H2TransactionIntegrationTest {

    private Session session;
    private SessionFactoryImpl sessionFactory;

    @BeforeEach
    void openSession() {
        YamlPropertyParser parser = new YamlPropertyParser();
        sessionFactory = new SessionFactoryImpl(parser, "h2", "session-factory-test-persistence.yml");
        session = sessionFactory.openSession();
    }

    @AfterEach
    void tearDown() {
        session.close();
        sessionFactory.close();
    }

    @Test
    @DisplayName("Entity is updated after commit transaction")
    void commit() {
        Person expectedPerson = new Person();
        expectedPerson.id(1L).name("John").age(42);

        session.begin();
        Person person = session.find(Person.class, 1L).orElseThrow();
        person.age(42);
        session.flush();
        session.commit();
        session.detach(person);

        Person foundPerson = session.find(Person.class, 1L).orElseThrow();
        assertThat(foundPerson).isEqualTo(expectedPerson);
    }

    @Test
    @DisplayName("Entity is not updated after rollback transaction")
    void rollback() {
        Person expectedPerson = new Person();
        expectedPerson.id(2L).name("Bilbo").age(129);

        session.begin();
        Person person = session.find(Person.class, 2L).orElseThrow();
        person.age(100);
        session.flush();
        session.rollback();
        session.detach(person);

        Person foundPerson = session.find(Person.class, 2L).orElseThrow();
        assertThat(foundPerson)
                .isEqualTo(expectedPerson)
                .isNotEqualTo(person);
    }

    @Test
    @DisplayName("Entity is not updated when transaction is not committed")
    void notFinishTransaction() {
        session.begin();
        Person person = session.find(Person.class, 1L).orElseThrow();
        person.age(333);
        session.flush();
        session.close();

        session = sessionFactory.openSession();
        Person samePersonFromDB = session.find(Person.class, 1L).orElseThrow();
        assertThat(person.age()).isNotEqualTo(samePersonFromDB.age());
    }
}
