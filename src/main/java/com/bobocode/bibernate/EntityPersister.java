package com.bobocode.bibernate;

import static com.bobocode.bibernate.Util.getColumnName;

import com.bobocode.bibernate.converter.AttributeConverter;
import com.bobocode.bibernate.converter.LocalDateTimeTypeConverter;
import com.bobocode.bibernate.converter.LocalDateTypeConverter;
import com.bobocode.bibernate.converter.LocalTimeTypeConverter;
import com.bobocode.bibernate.converter.ZonedDateTimeTypeConverter;
import com.bobocode.bibernate.exception.BibernateSQLException;
import com.bobocode.bibernate.exception.EntityMappingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EntityPersister {

    private final Connection connection;

    private final Map<Class<?>, Supplier<AttributeConverter<?>>> CONVERTERS =
            Map.of(LocalDate.class, LocalDateTypeConverter::new,
                    LocalTime.class, LocalTimeTypeConverter::new,
                    LocalDateTime.class, LocalDateTimeTypeConverter::new,
                    ZonedDateTime.class, ZonedDateTimeTypeConverter::new);

    public <T> List<T> select(Class<T> type, String query, List<Object> columnValuesToFilter) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < columnValuesToFilter.size(); i++) {
                statement.setObject(i + 1, columnValuesToFilter.get(i));
            }
            log.trace(statement.toString());

            ResultSet resultSet = statement.executeQuery();
            return processResultSet(type, resultSet);
        } catch (SQLException e) {
            throw new BibernateSQLException("Error loading data from DB", e);
        }
    }

    private <T> List<T> processResultSet(Class<T> type, ResultSet resultSet) {
        try {
            return mapResultSetToEntityList(type, resultSet);
        } catch (SQLException e) {
            throw new BibernateSQLException("Error parsing data got from DB", e);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new EntityMappingException("Entity mapping error", e);
        }
    }

    @SuppressWarnings("java:S3011")
    private <T> List<T> mapResultSetToEntityList(Class<T> type, ResultSet resultSet)
            throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            T obj = type.getConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                String columnName = getColumnName(field);
                Object value = resultSet.getObject(columnName);
                field.setAccessible(true);
                field.set(obj, convertToJavaType(field, value));
            }
            resultList.add(obj);
        }
        return resultList;
    }

    public void insert(String query, List<Object> valuesToInsert) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < valuesToInsert.size(); i++) {
                statement.setObject(i + 1, valuesToInsert.get(i));
            }
            System.out.println(statement);
            log.trace(statement.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BibernateSQLException("Error inserting data from DB", e);
        }
    }

    public void delete(String query, Object id) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            log.trace(statement.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BibernateSQLException("Error deleting data from DB", e);
        }
    }

    public void update(String updateQuery, List<Object> updatedColumValues, List<Object> columnsValuesToFilter) {
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            setValuesForUpdateStatement(updatedColumValues, columnsValuesToFilter, statement);
            log.trace(statement.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BibernateSQLException("Error updating data in DB", e);
        }
    }

    private static void setValuesForUpdateStatement(List<Object> updatedColumValues,
            List<Object> columnsValuesToFilter,
            PreparedStatement statement) throws SQLException {
        int i = 0;
        while (i < updatedColumValues.size()) {
            statement.setObject(i + 1, updatedColumValues.get(i));
            i++;
        }
        int j = 0;
        while (j < columnsValuesToFilter.size()) {
            statement.setObject(i + 1, columnsValuesToFilter.get(j));
            i++;
            j++;
        }
    }

    private Object convertToJavaType(Field field, Object value) throws IllegalAccessException {
        Supplier<AttributeConverter<?>> converterSupplier = CONVERTERS.get(field.getType());
        if (converterSupplier != null) {
            AttributeConverter<?> converter = converterSupplier.get();
            if (converter.isConvertable(value)) {
                return converter.convertToEntityAttribute(value);
            }
        }
        return value;
    }
}
