package com.bobocode.bibernate.configuration.dialects;

import com.bobocode.bibernate.configuration.Dialect;

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
