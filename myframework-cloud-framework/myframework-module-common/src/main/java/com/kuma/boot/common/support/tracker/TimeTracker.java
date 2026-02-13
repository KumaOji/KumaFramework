/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tracker;

public class TimeTracker
implements AutoCloseable {
    private final String operationName;
    private final long startTime;
    private final boolean logEnabled;

    public TimeTracker(String operationName) {
        this(operationName, true);
    }

    private TimeTracker(String operationName, boolean logEnabled) {
        this.operationName = operationName;
        this.startTime = System.nanoTime();
        this.logEnabled = logEnabled;
        if (logEnabled) {
            System.out.printf("\u5f00\u59cb\u6267\u884c: %s%n", operationName);
        }
    }

    public static TimeTracker of(String operationName) {
        return new TimeTracker(operationName);
    }

    public static <T> T track(String operationName, ThrowableSupplier<T> execution) {
        try {
            return TimeTracker.trackThrows(operationName, execution);
        }
        catch (Exception e) {
            throw new RuntimeException("\u6267\u884c\u5931\u8d25: " + operationName, e);
        }
    }

    public static <T> T trackThrows(String operationName, ThrowableSupplier<T> execution) throws Exception {
        try (TimeTracker ignored = new TimeTracker(operationName, true);){
            T t = execution.get();
            return t;
        }
    }

    public static void track(String operationName, ThrowableRunnable execution) {
        try {
            TimeTracker.trackThrows(operationName, execution);
        }
        catch (Exception e) {
            throw new RuntimeException("\u6267\u884c\u5931\u8d25: " + operationName, e);
        }
    }

    public static void trackThrows(String operationName, ThrowableRunnable execution) throws Exception {
        try (TimeTracker ignored = new TimeTracker(operationName, true);){
            execution.run();
        }
    }

    @Override
    public void close() {
        if (this.logEnabled) {
            long timeElapsed = (System.nanoTime() - this.startTime) / 1000000L;
            System.out.printf("%s \u6267\u884c\u5b8c\u6210\uff0c\u8017\u65f6: %d ms%n", this.operationName, timeElapsed);
        }
    }

    @FunctionalInterface
    public static interface ThrowableSupplier<T> {
        public T get() throws Exception;
    }

    @FunctionalInterface
    public static interface ThrowableRunnable {
        public void run() throws Exception;
    }
}

