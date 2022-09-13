package com.bobocode.bibernate;

import java.util.Set;
import java.util.stream.Collectors;

public interface Dialect {
    String SELECT_ALL_TEMPLATE = "select * from %s";
    String SELECT_ALL_ID_TEMPLATE = "select * from %s where id = ?";
    String SELECT_ALL_BY_PROPERTIES_TEMPLATE = "select * from %s where %s";
    String UPDATE_TEMPLATE = "update %s set %s where %s";
    String INSERT_TEMPLATE = "insert into %s (%s) values (%s)";
    String AND_WITH_SPACES = " AND ";
    String NAME_EQUALS_VALUE_TEMPLATE = "%s = ?";
    String PLACEHOLDER = "?";
    String COMMA_SEPARATOR = ", ";
    String DELETE_QUERY = "delete from %s where id = ?";

    static String prepareWhereClause(Set<String> columns) {
        return columns.stream()
                .sorted()
                .map(NAME_EQUALS_VALUE_TEMPLATE::formatted)
                .collect(Collectors.joining(AND_WITH_SPACES));
    }

    static String prepareSetClause(Set<String> columns) {
        return columns.stream()
                .sorted()
                .map(NAME_EQUALS_VALUE_TEMPLATE::formatted)
                .collect(Collectors.joining(AND_WITH_SPACES));
    }

    static String prepareValuesClause(Set<String> columns) {
        return columns.stream()
                .sorted()
                .collect(Collectors.joining(COMMA_SEPARATOR));
    }

    String getLimitClause(int limit, int offset);

    String getLimitClause(int limit);

}
