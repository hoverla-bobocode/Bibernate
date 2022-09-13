package com.bobocode.bibernate.transaction;

public interface Transaction {

    void begin();
    void commit();
    void rollback();

}
