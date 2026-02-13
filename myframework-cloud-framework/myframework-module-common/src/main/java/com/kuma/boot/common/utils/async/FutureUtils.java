/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  jakarta.annotation.Nullable
 */
package com.kuma.boot.common.utils.async;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.kuma.boot.common.utils.async.Deadline;
import com.kuma.boot.common.utils.async.ExecutorThreadFactory;
import com.kuma.boot.common.utils.async.Executors;
import com.kuma.boot.common.utils.async.FatalExitExceptionHandler;
import com.kuma.boot.common.utils.async.RetryStrategy;
import com.kuma.boot.common.utils.async.RunnableWithException;
import com.kuma.boot.common.utils.async.ScheduledExecutor;
import com.kuma.boot.common.utils.async.SupplierWithException;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FutureUtils {
    private static final CompletableFuture<Void> COMPLETED_VOID_FUTURE = CompletableFuture.completedFuture(null);
    private static final CompletableFuture<?> UNSUPPORTED_OPERATION_FUTURE = FutureUtils.completedExceptionally(new UnsupportedOperationException("This method is unsupported."));

    public static CompletableFuture<Void> completedVoidFuture() {
        return COMPLETED_VOID_FUTURE;
    }

    public static <T> CompletableFuture<T> unsupportedOperationFuture() {
        CompletableFuture<?> completableFuture = UNSUPPORTED_OPERATION_FUTURE;
        return completableFuture;
    }

    public static <T> void completeFromCallable(CompletableFuture<T> future, Callable<T> operation) {
        try {
            future.complete(operation.call());
        }
        catch (Exception e) {
            future.completeExceptionally(e);
        }
    }

    public static <T> CompletableFuture<T> retry(Supplier<CompletableFuture<T>> operation, int retries, Executor executor) {
        return FutureUtils.retry(operation, retries, ignore -> true, executor);
    }

    public static <T> CompletableFuture<T> retry(Supplier<CompletableFuture<T>> operation, int retries, Predicate<Throwable> retryPredicate, Executor executor) {
        CompletableFuture resultFuture = new CompletableFuture();
        FutureUtils.retryOperation(resultFuture, operation, retries, retryPredicate, executor);
        return resultFuture;
    }

    private static <T> void retryOperation(CompletableFuture<T> resultFuture, Supplier<CompletableFuture<T>> operation, int retries, Predicate<Throwable> retryPredicate, Executor executor) {
        if (!resultFuture.isDone()) {
            CompletableFuture operationFuture = operation.get();
            operationFuture.whenCompleteAsync((t, throwable) -> {
                if (throwable != null) {
                    if (throwable instanceof CancellationException) {
                        resultFuture.completeExceptionally(new RetryException("Operation future was cancelled.", (Throwable)throwable));
                    } else if (!retryPredicate.test((Throwable)throwable)) {
                        resultFuture.completeExceptionally(new RetryException("Stopped retrying the operation because the error is not retryable.", (Throwable)throwable));
                    } else if (retries > 0) {
                        FutureUtils.retryOperation(resultFuture, operation, retries - 1, retryPredicate, executor);
                    } else {
                        resultFuture.completeExceptionally(new RetryException("Could not complete the operation. Number of retries has been exhausted.", (Throwable)throwable));
                    }
                } else {
                    resultFuture.complete(t);
                }
            }, executor);
            resultFuture.whenComplete((t, throwable) -> operationFuture.cancel(false));
        }
    }

    public static <T> CompletableFuture<T> retryWithDelay(Supplier<CompletableFuture<T>> operation, RetryStrategy retryStrategy, Predicate<Throwable> retryPredicate, ScheduledExecutor scheduledExecutor) {
        CompletableFuture resultFuture = new CompletableFuture();
        FutureUtils.retryOperationWithDelay(resultFuture, operation, retryStrategy, retryPredicate, scheduledExecutor);
        return resultFuture;
    }

    public static <T> CompletableFuture<T> retryWithDelay(Supplier<CompletableFuture<T>> operation, RetryStrategy retryStrategy, ScheduledExecutor scheduledExecutor) {
        return FutureUtils.retryWithDelay(operation, retryStrategy, throwable -> true, scheduledExecutor);
    }

    private static <T> void retryOperationWithDelay(CompletableFuture<T> resultFuture, Supplier<CompletableFuture<T>> operation, RetryStrategy retryStrategy, Predicate<Throwable> retryPredicate, ScheduledExecutor scheduledExecutor) {
        if (!resultFuture.isDone()) {
            CompletableFuture operationResultFuture = operation.get();
            operationResultFuture.whenComplete((t, throwable) -> {
                if (throwable != null) {
                    if (throwable instanceof CancellationException) {
                        resultFuture.completeExceptionally(new RetryException("Operation future was cancelled.", (Throwable)throwable));
                    } else if (!retryPredicate.test((Throwable)throwable)) {
                        resultFuture.completeExceptionally((Throwable)throwable);
                    } else if (retryStrategy.getNumRemainingRetries() > 0) {
                        long retryDelayMillis = retryStrategy.getRetryDelay().toMillis();
                        ScheduledFuture<?> scheduledFuture = scheduledExecutor.schedule(() -> FutureUtils.lambda$retryOperationWithDelay$1(resultFuture, (Supplier)operation, retryStrategy, retryPredicate, scheduledExecutor), retryDelayMillis, TimeUnit.MILLISECONDS);
                        resultFuture.whenComplete((innerT, innerThrowable) -> scheduledFuture.cancel(false));
                    } else {
                        RetryException retryException = new RetryException("Could not complete the operation. Number of retries has been exhausted.", (Throwable)throwable);
                        resultFuture.completeExceptionally(retryException);
                    }
                } else {
                    resultFuture.complete(t);
                }
            });
            resultFuture.whenComplete((t, throwable) -> operationResultFuture.cancel(false));
        }
    }

    public static <T> CompletableFuture<T> retrySuccessfulWithDelay(Supplier<CompletableFuture<T>> operation, Duration retryDelay, Deadline deadline, Predicate<T> acceptancePredicate, ScheduledExecutor scheduledExecutor) {
        CompletableFuture resultFuture = new CompletableFuture();
        FutureUtils.retrySuccessfulOperationWithDelay(resultFuture, operation, retryDelay, deadline, acceptancePredicate, scheduledExecutor);
        return resultFuture;
    }

    private static <T> void retrySuccessfulOperationWithDelay(CompletableFuture<T> resultFuture, Supplier<CompletableFuture<T>> operation, Duration retryDelay, Deadline deadline, Predicate<T> acceptancePredicate, ScheduledExecutor scheduledExecutor) {
        if (!resultFuture.isDone()) {
            CompletableFuture operationResultFuture = operation.get();
            operationResultFuture.whenComplete((t, throwable) -> {
                if (throwable != null) {
                    if (throwable instanceof CancellationException) {
                        resultFuture.completeExceptionally(new RetryException("Operation future was cancelled.", (Throwable)throwable));
                    } else {
                        resultFuture.completeExceptionally((Throwable)throwable);
                    }
                } else if (acceptancePredicate.test(t)) {
                    resultFuture.complete(t);
                } else if (deadline.hasTimeLeft()) {
                    ScheduledFuture<?> scheduledFuture = scheduledExecutor.schedule(() -> FutureUtils.lambda$retrySuccessfulOperationWithDelay$1(resultFuture, (Supplier)operation, retryDelay, deadline, acceptancePredicate, scheduledExecutor), retryDelay.toMillis(), TimeUnit.MILLISECONDS);
                    resultFuture.whenComplete((innerT, innerThrowable) -> scheduledFuture.cancel(false));
                } else {
                    resultFuture.completeExceptionally(new RetryException("Could not satisfy the predicate within the allowed time."));
                }
            });
            resultFuture.whenComplete((t, throwable) -> operationResultFuture.cancel(false));
        }
    }

    public static <T> CompletableFuture<T> orTimeout(CompletableFuture<T> future, long timeout, TimeUnit timeUnit, @Nullable String timeoutMsg) {
        return FutureUtils.orTimeout(future, timeout, timeUnit, Executors.directExecutor(), timeoutMsg);
    }

    public static <T> CompletableFuture<T> orTimeout(CompletableFuture<T> future, long timeout, TimeUnit timeUnit, Executor timeoutFailExecutor, @Nullable String timeoutMsg) {
        if (!future.isDone()) {
            ScheduledFuture<?> timeoutFuture = Delayer.delay(() -> timeoutFailExecutor.execute(new Timeout(future, timeoutMsg)), timeout, timeUnit);
            future.whenComplete((value, throwable) -> {
                if (!timeoutFuture.isDone()) {
                    timeoutFuture.cancel(false);
                }
            });
        }
        return future;
    }

    public static <T> void completeDelayed(CompletableFuture<T> future, T success, Duration delay) {
        Delayer.delay(() -> future.complete(success), delay.toMillis(), TimeUnit.MILLISECONDS);
    }

    public static <T> T runIfNotDoneAndGet(RunnableFuture<T> future) throws ExecutionException, InterruptedException {
        if (null == future) {
            return null;
        }
        if (!future.isDone()) {
            future.run();
        }
        return (T)future.get();
    }

    public static CompletableFuture<Void> runAfterwards(CompletableFuture<?> future, RunnableWithException runnable) {
        return FutureUtils.runAfterwardsAsync(future, runnable, Executors.directExecutor());
    }

    public static CompletableFuture<Void> runAfterwardsAsync(CompletableFuture<?> future, RunnableWithException runnable) {
        return FutureUtils.runAfterwardsAsync(future, runnable, ForkJoinPool.commonPool());
    }

    public static CompletableFuture<Void> runAfterwardsAsync(CompletableFuture<?> future, RunnableWithException runnable, Executor executor) {
        CompletableFuture<Void> resultFuture = new CompletableFuture<Void>();
        future.whenCompleteAsync((ignored, throwable) -> {
            try {
                runnable.run();
            }
            catch (Throwable throwable2) {
                // empty catch block
            }
            if (throwable != null) {
                resultFuture.completeExceptionally((Throwable)throwable);
            } else {
                resultFuture.complete(null);
            }
        }, executor);
        return resultFuture;
    }

    public static CompletableFuture<Void> composeAfterwards(CompletableFuture<?> future, Supplier<CompletableFuture<?>> composedAction) {
        CompletableFuture<Void> resultFuture = new CompletableFuture<Void>();
        future.whenComplete((outerIgnored, outerThrowable) -> {
            CompletableFuture composedActionFuture = (CompletableFuture)composedAction.get();
            composedActionFuture.whenComplete((innerIgnored, innerThrowable) -> {
                if (innerThrowable != null) {
                    resultFuture.completeExceptionally(ExceptionUtils.firstOrSuppressed(innerThrowable, outerThrowable));
                } else if (outerThrowable != null) {
                    resultFuture.completeExceptionally((Throwable)outerThrowable);
                } else {
                    resultFuture.complete(null);
                }
            });
        });
        return resultFuture;
    }

    public static <T> ConjunctFuture<Collection<T>> combineAll(Collection<? extends CompletableFuture<? extends T>> futures) {
        Preconditions.checkNotNull(futures, (Object)"futures");
        return new ResultConjunctFuture(futures);
    }

    public static ConjunctFuture<Void> waitForAll(Collection<? extends CompletableFuture<?>> futures) {
        Preconditions.checkNotNull(futures, (Object)"futures");
        return new WaitingConjunctFuture(futures);
    }

    public static ConjunctFuture<Void> completeAll(Collection<? extends CompletableFuture<?>> futuresToComplete) {
        return new CompletionConjunctFuture(futuresToComplete);
    }

    public static <T> CompletableFuture<T> completedExceptionally(Throwable cause) {
        CompletableFuture result = new CompletableFuture();
        result.completeExceptionally(cause);
        return result;
    }

    public static <T> CompletableFuture<T> supplyAsync(SupplierWithException<T, ?> supplier, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            }
            catch (Throwable e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public static CompletableFuture<Void> runAsync(RunnableWithException runnable, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            }
            catch (Throwable e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public static <IN, OUT> CompletableFuture<OUT> thenApplyAsyncIfNotDone(CompletableFuture<IN> completableFuture, Executor executor, Function<? super IN, ? extends OUT> applyFun) {
        return completableFuture.isDone() ? completableFuture.thenApply(applyFun) : completableFuture.thenApplyAsync(applyFun, executor);
    }

    public static <IN, OUT> CompletableFuture<OUT> thenComposeAsyncIfNotDone(CompletableFuture<IN> completableFuture, Executor executor, Function<? super IN, ? extends CompletionStage<OUT>> composeFun) {
        return completableFuture.isDone() ? completableFuture.thenCompose(composeFun) : completableFuture.thenComposeAsync(composeFun, executor);
    }

    public static <IN> CompletableFuture<IN> whenCompleteAsyncIfNotDone(CompletableFuture<IN> completableFuture, Executor executor, BiConsumer<? super IN, ? super Throwable> whenCompleteFun) {
        return completableFuture.isDone() ? completableFuture.whenComplete(whenCompleteFun) : completableFuture.whenCompleteAsync(whenCompleteFun, executor);
    }

    public static <IN> CompletableFuture<Void> thenAcceptAsyncIfNotDone(CompletableFuture<IN> completableFuture, Executor executor, Consumer<? super IN> consumer) {
        return completableFuture.isDone() ? completableFuture.thenAccept(consumer) : completableFuture.thenAcceptAsync(consumer, executor);
    }

    public static <IN, OUT> CompletableFuture<OUT> handleAsyncIfNotDone(CompletableFuture<IN> completableFuture, Executor executor, BiFunction<? super IN, Throwable, ? extends OUT> handler) {
        return completableFuture.isDone() ? completableFuture.handle(handler) : completableFuture.handleAsync(handler, executor);
    }

    public static boolean isCompletedNormally(CompletableFuture<?> future) {
        return future.isDone() && !future.isCompletedExceptionally();
    }

    public static <T> T checkStateAndGet(CompletableFuture<T> future) {
        return FutureUtils.getWithoutException(future);
    }

    @Nullable
    public static <T> T getWithoutException(CompletableFuture<T> future) {
        if (FutureUtils.isCompletedNormally(future)) {
            try {
                return future.get();
            }
            catch (InterruptedException | ExecutionException exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static <T> T getOrDefault(CompletableFuture<T> future, T defaultValue) {
        T value = FutureUtils.getWithoutException(future);
        return value == null ? defaultValue : value;
    }

    public static void assertNoException(CompletableFuture<?> completableFuture) {
        FutureUtils.handleUncaughtException(completableFuture, FatalExitExceptionHandler.INSTANCE);
    }

    public static <T, E extends Throwable> CompletableFuture<T> handleException(CompletableFuture<? extends T> completableFuture, Class<E> exceptionClass, Function<? super E, ? extends T> exceptionHandler) {
        CompletableFuture handledFuture = new CompletableFuture();
        ((CompletableFuture)Preconditions.checkNotNull(completableFuture)).whenComplete((result, throwable) -> {
            if (throwable == null) {
                handledFuture.complete(result);
            } else if (exceptionClass.isAssignableFrom(throwable.getClass())) {
                Throwable exception = (Throwable)exceptionClass.cast(throwable);
                try {
                    handledFuture.complete(exceptionHandler.apply((Object)exception));
                }
                catch (Throwable t) {
                    handledFuture.completeExceptionally(t);
                }
            } else {
                handledFuture.completeExceptionally((Throwable)throwable);
            }
        });
        return handledFuture;
    }

    public static void handleUncaughtException(CompletableFuture<?> completableFuture, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        FutureUtils.handleUncaughtException(completableFuture, uncaughtExceptionHandler, FatalExitExceptionHandler.INSTANCE);
    }

    @VisibleForTesting
    static void handleUncaughtException(CompletableFuture<?> completableFuture, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Thread.UncaughtExceptionHandler fatalErrorHandler) {
        ((CompletableFuture)Preconditions.checkNotNull(completableFuture)).whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                Thread currentThread = Thread.currentThread();
                try {
                    uncaughtExceptionHandler.uncaughtException(currentThread, (Throwable)throwable);
                }
                catch (Throwable t) {
                    IllegalStateException errorHandlerException = new IllegalStateException("An error occurred while executing the error handling for a " + throwable.getClass().getSimpleName() + ".", t);
                    errorHandlerException.addSuppressed((Throwable)throwable);
                    fatalErrorHandler.uncaughtException(currentThread, errorHandlerException);
                }
            }
        });
    }

    public static <T> void forward(CompletableFuture<T> source, CompletableFuture<T> target) {
        source.whenComplete((BiConsumer)FutureUtils.forwardTo(target));
    }

    public static <T> void forwardAsync(CompletableFuture<T> source, CompletableFuture<T> target, Executor executor) {
        source.whenCompleteAsync((BiConsumer)FutureUtils.forwardTo(target), executor);
    }

    public static void throwIfCompletedExceptionally(CompletableFuture<?> future) throws Exception {
        if (future.isCompletedExceptionally()) {
            future.get();
        }
    }

    private static <T> BiConsumer<T, Throwable> forwardTo(CompletableFuture<T> target) {
        return (value, throwable) -> FutureUtils.doForward(value, throwable, target);
    }

    public static <T> void doForward(@Nullable T value, @Nullable Throwable throwable, CompletableFuture<T> target) {
        if (throwable != null) {
            target.completeExceptionally(throwable);
        } else {
            target.complete(value);
        }
    }

    public static <T> CompletableFuture<T> switchExecutor(CompletableFuture<? extends T> source, Executor executor) {
        return source.handleAsync((t, throwable) -> {
            if (throwable != null) {
                throw new CompletionException((Throwable)throwable);
            }
            return t;
        }, executor);
    }

    private static /* synthetic */ void lambda$retrySuccessfulOperationWithDelay$1(CompletableFuture resultFuture, Supplier operation, Duration retryDelay, Deadline deadline, Predicate acceptancePredicate, ScheduledExecutor scheduledExecutor) {
        FutureUtils.retrySuccessfulOperationWithDelay(resultFuture, operation, retryDelay, deadline, acceptancePredicate, scheduledExecutor);
    }

    private static /* synthetic */ void lambda$retryOperationWithDelay$1(CompletableFuture resultFuture, Supplier operation, RetryStrategy retryStrategy, Predicate retryPredicate, ScheduledExecutor scheduledExecutor) {
        FutureUtils.retryOperationWithDelay(resultFuture, operation, retryStrategy.getNextRetryStrategy(), retryPredicate, scheduledExecutor);
    }

    private static final class Delayer
    extends Enum<Delayer> {
        static final ScheduledThreadPoolExecutor DELAYER;
        private static final /* synthetic */ Delayer[] $VALUES;

        public static Delayer[] values() {
            return (Delayer[])$VALUES.clone();
        }

        public static Delayer valueOf(String name) {
            return Enum.valueOf(Delayer.class, name);
        }

        private static ScheduledFuture<?> delay(Runnable runnable, long delay, TimeUnit timeUnit) {
            Preconditions.checkNotNull((Object)runnable);
            Preconditions.checkNotNull((Object)((Object)timeUnit));
            return DELAYER.schedule(runnable, delay, timeUnit);
        }

        private static /* synthetic */ Delayer[] $values() {
            return new Delayer[0];
        }

        static {
            $VALUES = Delayer.$values();
            DELAYER = new ScheduledThreadPoolExecutor(1, new ExecutorThreadFactory("FlinkCompletableFutureDelayScheduler"));
        }
    }

    private static class ResultConjunctFuture<T>
    extends ConjunctFuture<Collection<T>> {
        private final int numTotal;
        private final AtomicInteger numCompleted = new AtomicInteger(0);
        private final T[] results;

        private void handleCompletedFuture(int index, T value, Throwable throwable) {
            if (throwable != null) {
                this.completeExceptionally(throwable);
            } else {
                this.results[index] = value;
                if (this.numCompleted.incrementAndGet() == this.numTotal) {
                    this.complete(Arrays.asList(this.results));
                }
            }
        }

        ResultConjunctFuture(Collection<? extends CompletableFuture<? extends T>> resultFutures) {
            this.numTotal = resultFutures.size();
            this.results = new Object[this.numTotal];
            if (resultFutures.isEmpty()) {
                this.complete(Collections.emptyList());
            } else {
                int counter = 0;
                for (CompletableFuture<T> future : resultFutures) {
                    int index = counter++;
                    future.whenComplete((T value, U throwable) -> this.handleCompletedFuture(index, (T)value, (Throwable)throwable));
                }
            }
        }

        @Override
        public int getNumFuturesTotal() {
            return this.numTotal;
        }

        @Override
        public int getNumFuturesCompleted() {
            return this.numCompleted.get();
        }
    }

    private static final class WaitingConjunctFuture
    extends ConjunctFuture<Void> {
        private final AtomicInteger numCompleted = new AtomicInteger(0);
        private final int numTotal;

        private void handleCompletedFuture(Object ignored, Throwable throwable) {
            if (throwable == null) {
                if (this.numTotal == this.numCompleted.incrementAndGet()) {
                    this.complete(null);
                }
            } else {
                this.completeExceptionally(throwable);
            }
        }

        private WaitingConjunctFuture(Collection<? extends CompletableFuture<?>> futures) {
            this.numTotal = futures.size();
            if (futures.isEmpty()) {
                this.complete(null);
            } else {
                for (CompletableFuture<?> future : futures) {
                    future.whenComplete(this::handleCompletedFuture);
                }
            }
        }

        @Override
        public int getNumFuturesTotal() {
            return this.numTotal;
        }

        @Override
        public int getNumFuturesCompleted() {
            return this.numCompleted.get();
        }
    }

    private static final class CompletionConjunctFuture
    extends ConjunctFuture<Void> {
        private final Object lock = new Object();
        private final int numFuturesTotal;
        private int futuresCompleted;
        private Throwable globalThrowable;

        private CompletionConjunctFuture(Collection<? extends CompletableFuture<?>> futuresToComplete) {
            this.numFuturesTotal = futuresToComplete.size();
            this.futuresCompleted = 0;
            this.globalThrowable = null;
            if (futuresToComplete.isEmpty()) {
                this.complete(null);
            } else {
                for (CompletableFuture<?> completableFuture : futuresToComplete) {
                    completableFuture.whenComplete(this::completeFuture);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void completeFuture(Object ignored, Throwable throwable) {
            Object object = this.lock;
            synchronized (object) {
                ++this.futuresCompleted;
                if (throwable != null) {
                    this.globalThrowable = ExceptionUtils.firstOrSuppressed(throwable, this.globalThrowable);
                }
                if (this.futuresCompleted == this.numFuturesTotal) {
                    if (this.globalThrowable != null) {
                        this.completeExceptionally(this.globalThrowable);
                    } else {
                        this.complete(null);
                    }
                }
            }
        }

        @Override
        public int getNumFuturesTotal() {
            return this.numFuturesTotal;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumFuturesCompleted() {
            Object object = this.lock;
            synchronized (object) {
                return this.futuresCompleted;
            }
        }
    }

    private static final class Timeout
    implements Runnable {
        private final CompletableFuture<?> future;
        private final String timeoutMsg;

        private Timeout(CompletableFuture<?> future, @Nullable String timeoutMsg) {
            this.future = (CompletableFuture)Preconditions.checkNotNull(future);
            this.timeoutMsg = timeoutMsg;
        }

        @Override
        public void run() {
            this.future.completeExceptionally(new TimeoutException(this.timeoutMsg));
        }
    }

    public static class RetryException
    extends Exception {
        private static final long serialVersionUID = 3613470781274141862L;

        public RetryException(String message) {
            super(message);
        }

        public RetryException(String message, Throwable cause) {
            super(message, cause);
        }

        public RetryException(Throwable cause) {
            super(cause);
        }
    }

    public static abstract class ConjunctFuture<T>
    extends CompletableFuture<T> {
        public abstract int getNumFuturesTotal();

        public abstract int getNumFuturesCompleted();
    }
}

