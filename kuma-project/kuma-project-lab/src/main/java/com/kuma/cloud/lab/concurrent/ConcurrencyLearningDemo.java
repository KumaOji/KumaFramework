package com.kuma.cloud.lab.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/** Independent JVM entry; every wait is bounded and every executor is shut down. */
public final class ConcurrencyLearningDemo {
    private ConcurrencyLearningDemo() {}

    public static void main(String[] args) throws Exception {
        String lesson = args.length == 0 ? "all" : args[0];
        switch (lesson) {
            case "monitor" -> LockExperiments.monitor();
            case "reentrant" -> LockExperiments.reentrant();
            case "readwrite" -> LockExperiments.readWrite();
            case "stamped" -> LockExperiments.stamped();
            case "semaphore" -> LockExperiments.semaphore();
            case "waitnotify" -> LockExperiments.waitNotify();
            case "deadlock" -> LockExperiments.deadlock();
            case "atomic" -> ConcurrentExperiments.atomic();
            case "coordination" -> ConcurrentExperiments.coordination();
            case "queue" -> ConcurrentExperiments.queue();
            case "future" -> ConcurrentExperiments.future();
            case "threadlocal" -> ConcurrentExperiments.threadLocal();
            case "locks" -> locks();
            case "concurrency" -> concurrency();
            case "all" -> { locks(); concurrency(); }
            default -> throw new IllegalArgumentException("Choose all|locks|concurrency|monitor|reentrant|readwrite|stamped|semaphore|waitnotify|deadlock|atomic|coordination|queue|future|threadlocal");
        }
        System.out.println("Java concurrency: " + lesson + " passed");
    }

    private static void locks() throws Exception {
        LockExperiments.monitor();
        LockExperiments.reentrant();
        LockExperiments.readWrite();
        LockExperiments.stamped();
        LockExperiments.semaphore();
        LockExperiments.waitNotify();
        LockExperiments.deadlock();
    }

    private static void concurrency() throws Exception {
        ConcurrentExperiments.atomic();
        ConcurrentExperiments.coordination();
        ConcurrentExperiments.queue();
        ConcurrentExperiments.future();
        ConcurrentExperiments.threadLocal();
    }

    static void check(String label, boolean condition, Object evidence) {
        System.out.println(label + " = " + evidence + "; check=" + condition);
        if (!condition) throw new IllegalStateException(label + ": " + evidence);
    }

    static void await(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Latch timed out");
    }

    static <T> T get(Future<T> future) throws Exception { return future.get(10, TimeUnit.SECONDS); }

    @FunctionalInterface interface Work { void run(ExecutorService pool) throws Exception; }

    static void workers(int size, Work work) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(size);
        try { work.run(pool); }
        finally {
            pool.shutdownNow();
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) throw new IllegalStateException("Workers did not terminate");
        }
    }

    static <T> Future<T> submit(ExecutorService pool, Callable<T> task) { return pool.submit(task); }
}
