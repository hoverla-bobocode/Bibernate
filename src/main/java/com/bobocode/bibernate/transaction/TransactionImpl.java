package com.bobocode.bibernate.transaction;

import com.bobocode.bibernate.exception.BibernateException;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

import static com.bobocode.bibernate.transaction.TransactionStatus.ACTIVE;
import static com.bobocode.bibernate.transaction.TransactionStatus.COMMITTED;
import static com.bobocode.bibernate.transaction.TransactionStatus.FAILED_COMMIT;
import static com.bobocode.bibernate.transaction.TransactionStatus.FAILED_ROLLBACK;
import static com.bobocode.bibernate.transaction.TransactionStatus.NOT_ACTIVE;
import static com.bobocode.bibernate.transaction.TransactionStatus.ROLLED_BACK;

@Slf4j
public class TransactionImpl implements Transaction {
    private Connection connection;

    @Setter(AccessLevel.PRIVATE)
    private TransactionStatus status;

    public TransactionImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void begin() {
        if (!NOT_ACTIVE.equals(status)) {
            throw new IllegalStateException("Cannot begin transaction with status %s".formatted(status));
        }
        log.trace("Begin transaction");
        try {
            connection.setAutoCommit(false);
            setStatus(ACTIVE);
        } catch (SQLException e) {
            throw new BibernateException("Error occurred while transaction beginning", e);
        }
    }

    @Override
    public void commit() {
        if (!ACTIVE.equals(status)) {
            throw new IllegalStateException("Cannot begin transaction with status %s".formatted(status));
        }
        log.trace("Commit transaction");
        try {
            connection.commit();
            setStatus(COMMITTED);
        } catch (SQLException e) {
            setStatus(FAILED_COMMIT);
            throw new BibernateException("Error occurred while transaction committing", e);
        }
    }

    @Override
    public void rollback() {
        if (!canRollback()) {
            throw new IllegalStateException("Cannot rollback transaction with status %s".formatted(status));
        }
        try {
            connection.rollback();
            setStatus(ROLLED_BACK);
        } catch (SQLException e) {
            setStatus(FAILED_ROLLBACK);
            throw new BibernateException("Error occurred while transaction rollback", e);
        }

    }

    private boolean canRollback() {
        return ACTIVE.equals(status) || FAILED_COMMIT.equals(status);
    }
}
