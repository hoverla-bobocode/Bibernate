package com.bobocode.bibernate;

public interface Transaction {
    void begin();
    void commit();
    void rollback();
}
