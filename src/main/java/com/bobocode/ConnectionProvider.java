package com.bobocode;

import java.sql.Connection;

public interface ConnectionProvider {
    Connection getConnection();
}
