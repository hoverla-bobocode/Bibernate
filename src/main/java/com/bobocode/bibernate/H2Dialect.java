package com.bobocode.bibernate;

public class H2Dialect implements Dialect {

    @Override
    public String getLimitClause(int limit, int offset) {
        return offset == 0 ? getLimitClause(limit) : " limit ? offset ?";
    }

    @Override
    public String getLimitClause(int limit) {
        return " limit ?";
    }
}
