package com.kuma.boot.tracer;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextScheduledExecutorService;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.context.ContextSnapshotFactory;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContextSnapshots {
   public static final ContextSnapshotFactory CONTEXT_SNAPSHOT_FACTORY = ContextSnapshotFactory.builder().contextRegistry(ContextRegistry.getInstance()).clearMissing(false).captureKeyPredicate((key) -> true).build();

   public ContextSnapshots() {
   }

   public static Supplier<ContextSnapshot> supplier() {
      ContextSnapshotFactory var10000 = CONTEXT_SNAPSHOT_FACTORY;
      Objects.requireNonNull(var10000);
      ContextSnapshotFactory var0 = var10000;
      return () -> var0.captureAll(new Object[0]);
   }

   public static ContextSnapshot contextSnapshot() {
      return CONTEXT_SNAPSHOT_FACTORY.captureAll(new Object[0]);
   }

   public static Runnable wrap(Runnable runnable) {
      return contextSnapshot().wrap(runnable);
   }

   public static <T> Callable<T> wrap(Callable<T> callable) {
      return contextSnapshot().wrap(callable);
   }

   public static <T> Consumer<T> wrap(Consumer<T> consumer) {
      return contextSnapshot().wrap(consumer);
   }

   public static Executor wrap(Executor executor) {
      return contextSnapshot().wrapExecutor(executor);
   }

   public static ExecutorService wrap(ExecutorService service) {
      return ContextExecutorService.wrap(service, supplier());
   }

   public static ScheduledExecutorService wrap(ScheduledExecutorService service) {
      return ContextScheduledExecutorService.wrap(service, supplier());
   }
}
