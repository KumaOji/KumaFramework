package com.kuma.cloud.lab.runtime;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/** Bounded mechanism experiments, independent of the Spring application. */
public final class RuntimeLearningDemo {
    private RuntimeLearningDemo() {}

    public static void main(String[] args) throws Exception {
        String lesson = args.length == 0 ? "all" : args[0];
        switch (lesson) {
            case "loader-failure" -> LoaderExperiments.failure();
            case "loader-identity" -> LoaderExperiments.identity();
            case "loader-context" -> LoaderExperiments.context();
            case "memory-regions" -> MemoryExperiments.regions();
            case "memory-retention" -> MemoryExperiments.retention();
            case "memory-lifetime" -> MemoryExperiments.lifetime();
            case "cpu-time" -> CpuExperiments.time();
            case "cpu-parallel" -> CpuExperiments.parallel();
            case "cpu-states" -> CpuExperiments.states();
            case "pool-growth" -> PoolExperiments.growth();
            case "pool-shutdown" -> PoolExperiments.shutdown();
            case "pool-virtual" -> PoolExperiments.virtual();
            case "classloading" -> classloading();
            case "memory" -> memory();
            case "cpu" -> cpu();
            case "threadpool" -> threadpool();
            case "all" -> { classloading(); memory(); cpu(); threadpool(); }
            default -> throw new IllegalArgumentException("Choose all|classloading|memory|cpu|threadpool or a lesson listed in runtime-resource-labs.md");
        }
        System.out.println("Java runtime: " + lesson + " passed");
    }

    private static void classloading() throws Exception { LoaderExperiments.failure(); LoaderExperiments.identity(); LoaderExperiments.context(); }
    private static void memory() throws Exception { MemoryExperiments.regions(); MemoryExperiments.retention(); MemoryExperiments.lifetime(); }
    private static void cpu() throws Exception { CpuExperiments.time(); CpuExperiments.parallel(); CpuExperiments.states(); }
    private static void threadpool() throws Exception { PoolExperiments.growth(); PoolExperiments.shutdown(); PoolExperiments.virtual(); }

    static void check(String label, boolean condition, Object evidence) {
        System.out.println(label + " = " + evidence + "; check=" + condition);
        if (!condition) throw new IllegalStateException(label + ": " + evidence);
    }
    static void await(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Latch timed out");
    }
    static <T> T get(Future<T> future) throws Exception { return future.get(10, TimeUnit.SECONDS); }
    static void until(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!condition.getAsBoolean()) {
            if (Thread.interrupted()) throw new InterruptedException();
            if (System.nanoTime() >= deadline) throw new IllegalStateException("Observation timed out");
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }
    @FunctionalInterface interface Work { void run(ExecutorService pool) throws Exception; }
    static void workers(int size, Work work) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(size);
        try { work.run(pool); }
        finally { stop(pool); }
    }
    static void stop(ExecutorService pool) throws InterruptedException {
        pool.shutdownNow();
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) throw new IllegalStateException("Workers did not terminate");
    }
}
