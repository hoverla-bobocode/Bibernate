package com.bobocode.bibernate;

import com.bobocode.bibernate.exception.QueryHelperException;
import com.bobocode.bibernate.session.Session;
import com.bobocode.bibernate.session.SessionFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Util class that provides methods for performing logic within session and transaction scope
 */
public class QueryHelper {

    private QueryHelper() {
    }

    /**
     * Creates a new session using the provided session factory {@link SessionFactory} and performs the passed action
     * within the session scope (see {@link Session}).
     * Provided logic is run within transaction scope (see {@link com.bobocode.bibernate.transaction.Transaction}),
     * which will be either committed or rollback (in case exception happens).
     * @param sessionFactory factory that creates session
     * @param action consumer that accepts session and perform logic
     */
    public static void runWithinTx(SessionFactory sessionFactory, Consumer<Session> action) {
        runWithinTxReturning(sessionFactory, session -> {
            action.accept(session);
            return null;
        });
    }

    /**
     * Creates a new session using the provided session factory {@link SessionFactory} and performs the passed action
     * within the session scope (see {@link Session}).
     * Provided logic is run within transaction scope (see {@link com.bobocode.bibernate.transaction.Transaction}),
     * which will be either committed or rollback (in case exception happens).
     * The result of action performing is returned.
     * @param sessionFactory factory that creates session
     * @param action function that accepts session, perform logic and return result
     * @return result of action performing
     * @param <T> type of returned value
     */
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
