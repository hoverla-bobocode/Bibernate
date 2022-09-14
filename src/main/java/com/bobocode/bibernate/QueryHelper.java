package com.bobocode.bibernate;

import com.bobocode.bibernate.exception.QueryHelperException;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class QueryHelper {

    private QueryHelper() {
    }

    public static void runWithinTx(SessionFactory sessionFactory, Consumer<Session> action) {
        runWithinTxReturning(sessionFactory, session -> {
            action.accept(session);
            return null;
        });
    }

    public static <T> T runWithinTxReturning(SessionFactory sessionFactory, Function<Session, T> action) {
        try (Session session = sessionFactory.openSession()) {
            session.begin();
            try {
                T result = action.apply(session);
                session.commit();
                return result;
            } catch (Exception e) {
                session.rollback();
                throw new QueryHelperException("Transaction is rolled back", e);
            }
        }
    }
}
