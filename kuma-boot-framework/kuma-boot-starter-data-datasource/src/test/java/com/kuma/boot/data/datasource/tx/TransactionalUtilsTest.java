package com.kuma.boot.data.datasource.tx;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.*;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionalUtilsTest {
    @Test
    void concurrentReadAndWriteKeepIndependentDefinitionsAndPreserveDefaults() throws Exception {
        var manager = mock(PlatformTransactionManager.class);
        var first = new AtomicReference<TransactionDefinition>();
        var readerStarted = new CountDownLatch(1);
        var writerFinished = new CountDownLatch(1);
        when(manager.getTransaction(any())).thenAnswer(call -> {
            var definition = call.getArgument(0, TransactionDefinition.class);
            if (definition.isReadOnly()) first.set(definition);
            return new SimpleTransactionStatus();
        });
        var defaults = new TransactionTemplate(manager);
        defaults.setTimeout(7);
        var transactions = new TransactionalUtils(defaults);
        try (var threads = Executors.newFixedThreadPool(2)) {
            var reader = threads.submit(() -> transactions.defaultExecute(status -> {
                readerStarted.countDown();
                try { assertThat(writerFinished.await(3, TimeUnit.SECONDS)).isTrue(); }
                catch (InterruptedException error) { throw new AssertionError(error); }
                assertThat(first.get().isReadOnly()).isTrue();
                assertThat(first.get().getIsolationLevel()).isEqualTo(TransactionDefinition.ISOLATION_REPEATABLE_READ);
                assertThat(first.get().getTimeout()).isEqualTo(7);
                return "read";
            }, TransactionDefinition.ISOLATION_REPEATABLE_READ, true));
            assertThat(readerStarted.await(3, TimeUnit.SECONDS)).isTrue();
            try {
                assertThat(threads.submit(() -> transactions.defaultExecute(status -> "write")).get(3, TimeUnit.SECONDS))
                        .isEqualTo("write");
            } finally { writerFinished.countDown(); }
            assertThat(reader.get(3, TimeUnit.SECONDS)).isEqualTo("read");
        }
        assertThat(defaults.isReadOnly()).isFalse();
        assertThat(defaults.getIsolationLevel()).isEqualTo(TransactionDefinition.ISOLATION_DEFAULT);
        assertThat(defaults.getTimeout()).isEqualTo(7);
    }

    @Test
    void nestedOverridesAndFailuresDoNotMutateTheOuterDefinition() {
        var manager = mock(PlatformTransactionManager.class);
        var definitions = new java.util.ArrayList<TransactionDefinition>();
        when(manager.getTransaction(any())).thenAnswer(call -> {
            definitions.add(call.getArgument(0));
            return new SimpleTransactionStatus();
        });
        var transactions = new TransactionalUtils(new TransactionTemplate(manager));
        var failure = new IllegalStateException("inner failure");
        transactions.defaultExecute(outer -> {
            assertThatThrownBy(() -> transactions.newExecute(inner -> { throw failure; })).isSameAs(failure);
            assertThat(definitions.getFirst().isReadOnly()).isTrue();
            assertThat(definitions.getFirst().getIsolationLevel()).isEqualTo(TransactionDefinition.ISOLATION_REPEATABLE_READ);
            return null;
        }, TransactionDefinition.ISOLATION_REPEATABLE_READ, true);
        verify(manager).rollback(any());
        verify(manager).commit(any());
    }
}
