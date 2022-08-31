package com.bobocode.bibernate;

import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.exception.EntityMappingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Validator {

    public static <T> void validateEntity(Class<T> type) {
        log.info("Validation of entity %s".formatted(type.getName()));
        List<String> messages = new ArrayList<>();
        checkIsEntity(type, messages);
        checkHasIdField(type, messages);

        if (!messages.isEmpty()) {
            messages.forEach(log::error);
            throw new EntityMappingException(String.join("\n", messages));
        }
    }

    private static <T> void checkIsEntity(Class<T> type, List<String> messages) {
        if (!type.isAnnotationPresent(Entity.class)) {
            messages.add("%s is not defined as entity".formatted(type.getName()));
        }
    }

    private static <T> void checkHasIdField(Class<T> type, List<String> messages) {
        Optional<Field> primaryKeyFieldOptional = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny();

        if (primaryKeyFieldOptional.isEmpty()) {
            messages.add("Entity class must have field annotated with @Id");
        }
    }

    public static <T> void checkIdValidPrimaryKeyType(Class<T> type, Object primaryKey) {
        Optional<Field> primaryKeyField = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny();
        if (primaryKeyField.isPresent() && !primaryKeyField.get().getType().isAssignableFrom(primaryKey.getClass())) {
            throw new IllegalArgumentException("[primaryKey] argument has not valid type for entity type %s".formatted(type.getName()));
        }
    }

    public static void checkNotNegativeNumber(long num, String message) {
        if (num < 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
