package com.bobocode.bibernate;

import com.bobocode.bibernate.exception.QueryHelperException;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactory;

import java.util.function.Function;

public class QueryHelper {
    private SessionFactory sessionFactory;

    public QueryHelper(SessionFactory sessionFactoryFactory) {
        this.sessionFactory = sessionFactoryFactory;
    }

    public <T> T readWithinTx(Function<Session, T> entityManagerConsumer) {
        Session session = sessionFactory.createSession();
        session.beginTransaction();
        try {
            T result = entityManagerConsumer.apply(session);
            session.commitTransaction();
            return result;
        } catch (Exception e) {
            session.rollbackTransaction();
            throw new QueryHelperException("Transaction is rolled back", e);
        } finally {
            session.closeTransaction();
        }
    }
}
