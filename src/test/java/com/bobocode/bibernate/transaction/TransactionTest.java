package com.bobocode.bibernate.transaction;

import com.bobocode.bibernate.exception.BibernateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static com.bobocode.bibernate.transaction.TransactionStatus.ACTIVE;
import static com.bobocode.bibernate.transaction.TransactionStatus.COMMITTED;
import static com.bobocode.bibernate.transaction.TransactionStatus.FAILED_COMMIT;
import static com.bobocode.bibernate.transaction.TransactionStatus.FAILED_ROLLBACK;
import static com.bobocode.bibernate.transaction.TransactionStatus.ROLLED_BACK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionTest {
    @InjectMocks
    public TransactionImpl transaction;

    @Mock
    private Connection connection;

    @Test
    void beginTransaction() throws SQLException {
        transaction.begin();
        verify(connection).setAutoCommit(false);
        assertThat(transaction.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void commitTransaction() throws SQLException {
        transaction.begin();

        transaction.commit();
        verify(connection).commit();
        assertThat(transaction.getStatus()).isEqualTo(COMMITTED);
    }

    @Test
    void rollbackTransaction() throws SQLException {
        transaction.begin();

        transaction.rollback();
        verify(connection).rollback();
        assertThat(transaction.getStatus()).isEqualTo(ROLLED_BACK);
    }

    @Test
    void commitTransactionFailed() throws SQLException {
        transaction.begin();

        doThrow(new SQLException()).when(connection).commit();
        assertThatThrownBy(() -> transaction.commit())
                .isInstanceOf(BibernateException.class)
                .hasMessage("Error occurred while transaction committing");
        assertThat(transaction.getStatus()).isEqualTo(FAILED_COMMIT);
    }

    @Test
    void rollbackTransactionFailed() throws SQLException {
        transaction.begin();

        doThrow(new SQLException()).when(connection).rollback();
        assertThatThrownBy(() -> transaction.rollback())
                .isInstanceOf(BibernateException.class)
                .hasMessage("Error occurred while transaction rollback");
        assertThat(transaction.getStatus()).isEqualTo(FAILED_ROLLBACK);
    }

    @Test
    @DisplayName("Throws IllegalStateException when try open new transaction before closing prev one")
    void throwsExceptionWhenTransactionAlreadyActive() {
        transaction.begin();

        assertThatThrownBy(transaction::begin)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Transaction is already active");
    }

    @Test
    @DisplayName("Throws IllegalStateException when not active transaction is committing")
    void throwsExceptionWhenNotActiveTransactionIsCommitting() {
        assertThatThrownBy(transaction::commit)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot commit not active transaction");
    }

    @Test
    @DisplayName("Throws IllegalStateException when not active transaction is rollback")
    void throwsExceptionWhenNotActiveTransactionIsRollback() {
        assertThatThrownBy(transaction::rollback)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot rollback transaction with status NOT_ACTIVE");
    }
}
