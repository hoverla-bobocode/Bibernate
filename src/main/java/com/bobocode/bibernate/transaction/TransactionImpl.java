package com.bobocode.bibernate.transaction;

import com.bobocode.bibernate.exception.BibernateException;
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
    private final Connection connection;

    private TransactionStatus status;

    public TransactionImpl(Connection connection) {
        this.connection = connection;
        this.status = NOT_ACTIVE;
    }

    @Override
    public void begin() {
        if (status == ACTIVE) {
            throw new IllegalStateException("Transaction is already active");
        }
        log.trace("Begin transaction");
        try {
            connection.setAutoCommit(false);
            status = ACTIVE;
        } catch (SQLException e) {
            throw new BibernateException("Error occurred while transaction beginning", e);
        }
    }

    @Override
    public void commit() {
        if (status != ACTIVE) {
            throw new IllegalStateException("Cannot commit not active transaction");
        }
        log.trace("Commit transaction");
        try {
            connection.commit();
            status = COMMITTED;
        } catch (SQLException e) {
            status = FAILED_COMMIT;
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
            status = ROLLED_BACK;
        } catch (SQLException e) {
            status = FAILED_ROLLBACK;
            throw new BibernateException("Error occurred while transaction rollback", e);
        }

    }

    public TransactionStatus getStatus() {
        return status;
    }

    private boolean canRollback() {
        return status == ACTIVE || status == FAILED_COMMIT;
    }
}
