package com.bobocode.bibernate;

import com.bobocode.bibernate.exception.BibernateSQLException;
import com.bobocode.bibernate.exception.EntityMappingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.bobocode.bibernate.Util.getColumnName;

@Slf4j
@AllArgsConstructor
public class EntityPersister {
    private final DataSource dataSource;

    public <T> List<T> select(Class<T> type, String query, List<Object> columnValuesToFilter) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < columnValuesToFilter.size(); i++) {
                    statement.setObject(i + 1, columnValuesToFilter.get(i));
                }
                log.trace(statement.toString());

                ResultSet resultSet = statement.executeQuery();
                return processResultSet(type, resultSet);
            }
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
    private static <T> List<T> mapResultSetToEntityList(Class<T> type, ResultSet resultSet)
            throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        List<T> resultList = new ArrayList<>();
        while (resultSet.next()) {
            T obj = type.getConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String columnName = getColumnName(field);
                field.set(obj, resultSet.getObject(columnName));
            }
            resultList.add(obj);
        }
        return resultList;
    }

    public void update(String updateQuery, List<Object> updatedColumValues, List<Object> columnsValuesToFilter) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                setValuesForUpdateStatement(updatedColumValues, columnsValuesToFilter, statement);
                log.trace(statement.toString());
                statement.executeUpdate();
            }
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
}
