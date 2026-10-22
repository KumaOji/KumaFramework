package com.kuma.boot.data.datasource.autoconfigure;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/** Transaction helpers sharing Spring's commit, rollback and exception semantics. */
public class TransactionAutoConfiguration {
    private final PlatformTransactionManager manager;
    private final TransactionDefinition definition;
    private final ThreadPoolExecutor executor;

    public TransactionAutoConfiguration(PlatformTransactionManager manager, TransactionDefinition definition,
                                        ThreadPoolExecutor executor) {
        this.manager = manager;
        this.definition = definition;
        this.executor = executor;
    }

    public void execute(Task task) {
        new TransactionTemplate(manager, definition).executeWithoutResult(status -> task.run());
    }

    public <T> Optional<T> execute(Supplier<T> supplier) {
        return Optional.ofNullable(new TransactionTemplate(manager, definition).execute(status -> supplier.get()));
    }

    /** The caller can observe completion and the original failure. */
    public CompletableFuture<Void> asyncExecute(Task task) {
        return CompletableFuture.runAsync(() -> execute(task), executor);
    }

    public <T> CompletableFuture<Optional<T>> submit(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> execute(supplier), executor);
    }

    /** Compatibility API: waits for the transaction and preserves failure/interrupt information. */
    public <T> Optional<T> asyncExecute(Supplier<T> supplier) {
        try {
            return submit(supplier).get();
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while awaiting transaction", error);
        } catch (ExecutionException error) {
            Throwable cause = error.getCause();
            if (cause instanceof RuntimeException runtime) throw runtime;
            if (cause instanceof Error fatal) throw fatal;
            throw new IllegalStateException("Asynchronous transaction failed", cause);
        }
    }

    @FunctionalInterface
    public interface Task { void run(); }
}
