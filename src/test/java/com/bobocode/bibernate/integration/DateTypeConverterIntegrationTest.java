package com.bobocode.bibernate.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.bibernate.integration.entity.DateTestEntity;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactoryImpl;
import com.bobocode.parser.YamlPropertyParser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class DateTypeConverterIntegrationTest {

    private Session session;
    private SessionFactoryImpl sessionFactory;

    @BeforeEach
    void openSession() {
        YamlPropertyParser parser = new YamlPropertyParser();
        sessionFactory = new SessionFactoryImpl(parser, "date-converter-integration-test");
        session = sessionFactory.openSession();
    }

    @AfterEach
    void tearDown() {
        session.close();
        sessionFactory.close();
    }

    @Test
    void localDateConverter_save() {
        Long id = ThreadLocalRandom.current().nextLong(5, 20);
        DateTestEntity dates = DateTestEntity.builder().id(ThreadLocalRandom.current().nextLong(1, 1000000))
                .localDate(LocalDate.now())
                .build();

        boolean cached = session.contains(dates);
        assertThat(cached).isFalse();

        session.save(dates);
        session.flush();
        cached = session.contains(dates);
        assertThat(cached).isTrue();
    }

    @Test
    void localTimeConverter_save() {
        Long id = ThreadLocalRandom.current().nextLong(5, 1000000);
        DateTestEntity dates = DateTestEntity.builder().id(id)
                .localTime(LocalTime.now())
                .build();

        boolean cached = session.contains(dates);
        assertThat(cached).isFalse();

        session.save(dates);
        session.flush();
        cached = session.contains(dates);
        assertThat(cached).isTrue();
    }

    @Test
    void localDateTimeConverter_save() {
        Long id = ThreadLocalRandom.current().nextLong(5, 1000000);
        DateTestEntity dates = DateTestEntity.builder().id(id)
                .localDateTime(LocalDateTime.now())
                .build();

        boolean cached = session.contains(dates);
        assertThat(cached).isFalse();

        session.save(dates);
        session.flush();
        cached = session.contains(dates);
        assertThat(cached).isTrue();
    }

    @Test
    void zonedDateTimeConverter_save() {
        Long id = ThreadLocalRandom.current().nextLong(5, 1000000);
        DateTestEntity dates = DateTestEntity.builder().id(id)
                .zonedDateTime(ZonedDateTime.now())
                .build();

        boolean cached = session.contains(dates);
        assertThat(cached).isFalse();

        session.save(dates);
        session.flush();
        cached = session.contains(dates);
        assertThat(cached).isTrue();
    }

    @Test
    void localDateConverter_find() {
        Long id = 1L;
        DateTestEntity expectedDates = DateTestEntity.builder().id(id)
                .localDate(LocalDate.parse("2022-09-15"))
                .build();

        Optional<DateTestEntity> dates = session.find(DateTestEntity.class, id);

        assertEquals(expectedDates, dates.orElse(null));
    }

    @Test
    void localTimeConverter_find() {
        Long id = 2L;
        DateTestEntity expectedDates = DateTestEntity.builder().id(id)
                .localTime(LocalTime.parse("11:45:17.181047"))
                .build();

        Optional<DateTestEntity> dates = session.find(DateTestEntity.class, id);

        assertEquals(expectedDates, dates.orElse(null));
    }

    @Test
    void localDateTimeConverter_find() {
        Long id = 3L;
        DateTestEntity expectedDates = DateTestEntity.builder().id(id)
                .localDateTime(LocalDateTime.parse("2022-09-15T11:46:37.809162"))
                .build();

        Optional<DateTestEntity> dates = session.find(DateTestEntity.class, id);

        assertEquals(expectedDates, dates.orElse(null));
    }

    @Test
    void zonedDateTimeConverter_find() {
        Long id = 4L;
        DateTestEntity expectedDates = DateTestEntity.builder().id(id)
                .zonedDateTime(ZonedDateTime.parse("2022-09-15T11:45:17.061287+02"))
                .build();

        Optional<DateTestEntity> dates = session.find(DateTestEntity.class, id);

        assertEquals(expectedDates, dates.orElse(null));
    }
}
