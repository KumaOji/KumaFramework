/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.Nonnull
 */
package com.kuma.boot.common.utils.async;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DirectExecutorService
implements ExecutorService {
    private final boolean triggerRejectedExecutionException;
    private boolean isShutdown = false;

    DirectExecutorService(boolean triggerRejectedExecutionException) {
        this.triggerRejectedExecutionException = triggerRejectedExecutionException;
    }

    @Override
    public void shutdown() {
        this.isShutdown = true;
    }

    @Override
    @Nonnull
    public List<Runnable> shutdownNow() {
        this.isShutdown = true;
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return this.isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return this.isShutdown;
    }

    @Override
    public boolean awaitTermination(long timeout, @Nonnull TimeUnit unit) {
        return this.isShutdown;
    }

    @Override
    @Nonnull
    public <T> Future<T> submit(@Nonnull Callable<T> task) {
        this.throwRejectedExecutionExceptionIfShutdown();
        try {
            T result = task.call();
            return new CompletedFuture<T>(result, null);
        }
        catch (Exception e) {
            return new CompletedFuture<Object>(null, e);
        }
    }

    @Override
    @Nonnull
    public <T> Future<T> submit(@Nonnull Runnable task, T result) {
        this.throwRejectedExecutionExceptionIfShutdown();
        task.run();
        return new CompletedFuture<T>(result, null);
    }

    @Override
    @Nonnull
    public Future<?> submit(@Nonnull Runnable task) {
        this.throwRejectedExecutionExceptionIfShutdown();
        task.run();
        return new CompletedFuture<Object>(null, null);
    }

    @Override
    @Nonnull
    public <T> List<Future<T>> invokeAll(@Nonnull Collection<? extends Callable<T>> tasks) {
        this.throwRejectedExecutionExceptionIfShutdown();
        ArrayList<Future<T>> result = new ArrayList<Future<T>>();
        for (Callable<T> task : tasks) {
            try {
                result.add(new CompletedFuture<T>(task.call(), null));
            }
            catch (Exception e) {
                result.add(new CompletedFuture<Object>(null, e));
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public <T> List<Future<T>> invokeAll(@Nonnull Collection<? extends Callable<T>> tasks, long timeout, @Nonnull TimeUnit unit) {
        this.throwRejectedExecutionExceptionIfShutdown();
        long end = System.currentTimeMillis() + unit.toMillis(timeout);
        Iterator<Callable<T>> iterator = tasks.iterator();
        ArrayList<Future<T>> result = new ArrayList<Future<T>>();
        while (end > System.currentTimeMillis() && iterator.hasNext()) {
            Callable<T> callable = iterator.next();
            try {
                result.add(new CompletedFuture<T>(callable.call(), null));
            }
            catch (Exception e) {
                result.add(new CompletedFuture<Object>(null, e));
            }
        }
        while (iterator.hasNext()) {
            iterator.next();
            result.add(new Future<T>(this){
                {
                    Objects.requireNonNull(this$0);
                }

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return true;
                }

                @Override
                public boolean isDone() {
                    return false;
                }

                @Override
                public T get() {
                    throw new CancellationException("Task has been cancelled.");
                }

                @Override
                public T get(long timeout, @Nonnull TimeUnit unit) {
                    throw new CancellationException("Task has been cancelled.");
                }
            });
        }
        return result;
    }

    @Override
    @Nonnull
    public <T> T invokeAny(@Nonnull Collection<? extends Callable<T>> tasks) throws ExecutionException {
        this.throwRejectedExecutionExceptionIfShutdown();
        Exception exception = null;
        for (Callable<T> task : tasks) {
            try {
                return task.call();
            }
            catch (Exception e) {
                exception = e;
            }
        }
        throw new ExecutionException("No tasks finished successfully.", exception);
    }

    @Override
    public <T> T invokeAny(@Nonnull Collection<? extends Callable<T>> tasks, long timeout, @Nonnull TimeUnit unit) throws ExecutionException, TimeoutException {
        this.throwRejectedExecutionExceptionIfShutdown();
        long end = System.currentTimeMillis() + unit.toMillis(timeout);
        Exception exception = null;
        Iterator<Callable<T>> iterator = tasks.iterator();
        while (end > System.currentTimeMillis() && iterator.hasNext()) {
            Callable<T> callable = iterator.next();
            try {
                return callable.call();
            }
            catch (Exception e) {
                exception = e;
            }
        }
        if (iterator.hasNext()) {
            throw new TimeoutException("Could not finish execution of tasks within time.");
        }
        throw new ExecutionException("No tasks finished successfully.", exception);
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        this.throwRejectedExecutionExceptionIfShutdown();
        command.run();
    }

    private void throwRejectedExecutionExceptionIfShutdown() {
        if (this.isShutdown() && this.triggerRejectedExecutionException) {
            throw new RejectedExecutionException("The ExecutorService is shut down already. No Callables can be executed.");
        }
    }

    static class CompletedFuture<V>
    implements Future<V> {
        private final V value;
        private final Exception exception;

        CompletedFuture(V value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public V get() throws ExecutionException {
            if (this.exception != null) {
                throw new ExecutionException(this.exception);
            }
            return this.value;
        }

        @Override
        public V get(long timeout, @Nonnull TimeUnit unit) throws ExecutionException {
            return this.get();
        }
    }
}

