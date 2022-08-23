package com.bobocode;

public interface Transaction {
    void begin();
    void commit();
    void rollback();
}
