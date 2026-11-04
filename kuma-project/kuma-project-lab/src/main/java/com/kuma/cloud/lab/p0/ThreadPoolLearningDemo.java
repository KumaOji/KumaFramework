package com.kuma.cloud.lab.p0;

import org.slf4j.MDC;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import static com.kuma.cloud.lab.p0.LabReport.*;

/** Fixed small pools, explicit handshakes and bounded waits; no changes to application pools. */
public final class ThreadPoolLearningDemo {
    private static final Semaphore SLOT = new Semaphore(1);
    private ThreadPoolLearningDemo() {}
    public static void main(String[] args) throws Exception {
        System.out.println(JsonMapper.builder().build().writeValueAsString(run(args.length == 0 ? "all" : args[0])));
    }
    public static LabReport run(String lesson) throws Exception {
        validate(lesson, "all", "queue", "exceptions", "cancellation", "starvation", "context", "bulkhead");
        if (!SLOT.tryAcquire()) throw new IllegalStateException("线程池实验正在运行，请稍后重试");
        try {
            var report = new Builder(lesson);
            if (selected(lesson, "queue")) queue(report);
            if (selected(lesson, "exceptions")) exceptions(report);
            if (selected(lesson, "cancellation")) cancellation(report);
            if (selected(lesson, "starvation")) starvation(report);
            if (selected(lesson, "context")) context(report);
            if (selected(lesson, "bulkhead")) bulkhead(report);
            return report.build();
        } finally { SLOT.release(); }
    }
    static void await(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("实验同步超时");
    }
    static void stop(ExecutorService pool) throws InterruptedException {
        pool.shutdownNow();
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) throw new IllegalStateException("实验线程未退出");
    }
    private static void queue(Builder r) throws Exception {
        var pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        try {
            var first = pool.submit(() -> { entered.countDown(); await(release); return 1; });
            await(entered);
            var queued = pool.submit(() -> 2);
            boolean rejected = false;
            try { pool.execute(() -> {}); } catch (RejectedExecutionException expected) { rejected = true; }
            r.check("boundedQueueRejectsOverflow", rejected && pool.getQueue().size() == 1);
            var caller = new AtomicReference<Thread>();
            pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            pool.execute(() -> caller.set(Thread.currentThread()));
            r.check("callerRunsBackpressure", caller.get() == Thread.currentThread());
            r.step("队列与拒绝", "1个工作线程、1个排队任务；AbortPolicy 抛异常，CallerRunsPolicy 在提交线程执行。", Map.of("active", pool.getActiveCount(), "queued", pool.getQueue().size()));
            release.countDown();
            r.check("acceptedTasksComplete", first.get(5, TimeUnit.SECONDS) == 1 && queued.get(5, TimeUnit.SECONDS) == 2);
        } finally { release.countDown(); stop(pool); }
    }
    private static void exceptions(Builder r) throws Exception {
        var uncaught = new AtomicReference<Throwable>();
        var observed = new CountDownLatch(1);
        var pool = Executors.newSingleThreadExecutor(task -> {
            var thread = new Thread(task, "lab-exception");
            thread.setUncaughtExceptionHandler((t, e) -> { uncaught.set(e); observed.countDown(); });
            return thread;
        });
        try {
            pool.execute(() -> { throw new IllegalStateException("execute-failure"); });
            await(observed);
            r.check("executeReachesUncaughtHandler", uncaught.get() instanceof IllegalStateException);
            var future = pool.submit(() -> { throw new IllegalArgumentException("submit-failure"); });
            try { future.get(5, TimeUnit.SECONDS); throw new AssertionError("Expected ExecutionException"); }
            catch (ExecutionException expected) { r.check("submitFailureRequiresGet", expected.getCause() instanceof IllegalArgumentException); }
            r.step("异常可见性", "execute 交给未捕获异常处理器；submit 把异常保存在 Future，必须检查结果。", List.of("execute-failure", "submit-failure"));
        } finally { stop(pool); }
    }
    private static void cancellation(Builder r) throws Exception {
        var pool = Executors.newSingleThreadExecutor();
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var interrupted = new CountDownLatch(1);
        try {
            var future = pool.submit(() -> {
                entered.countDown();
                try { await(release); } catch (InterruptedException e) { interrupted.countDown(); Thread.currentThread().interrupt(); }
            });
            await(entered);
            try { future.get(50, TimeUnit.MILLISECONDS); throw new AssertionError("Expected timeout"); }
            catch (TimeoutException expected) { r.check("getTimeoutDoesNotCancel", !future.isDone() && !future.isCancelled()); }
            r.check("cancelAccepted", future.cancel(true));
            await(interrupted);
            r.check("interruptObserved", future.isCancelled());
            r.step("超时与取消", "get(timeout) 只结束等待；cancel(true) 请求中断，任务需响应中断。", Map.of("cancelled", future.isCancelled()));
        } finally { release.countDown(); stop(pool); }
    }
    private static void starvation(Builder r) throws Exception {
        var pool = Executors.newSingleThreadExecutor();
        try {
            var timedOut = pool.submit(() -> {
                var child = pool.submit(() -> 42);
                try { child.get(100, TimeUnit.MILLISECONDS); return false; }
                catch (TimeoutException expected) { return true; }
                finally { child.cancel(true); }
            });
            r.check("nestedWaitStarvesSingleWorker", timedOut.get(5, TimeUnit.SECONDS));
            var composed = CompletableFuture.supplyAsync(() -> 21, pool).thenApplyAsync(n -> n * 2, pool);
            r.check("compositionWithoutBlockingCompletes", composed.get(5, TimeUnit.SECONDS) == 42);
            r.step("线程饥饿与组合", "单线程中等待同池子任务导致子任务无法开始；改为非阻塞阶段组合。", Map.of("composedResult", 42));
        } finally { stop(pool); }
    }
    private static void context(Builder r) throws Exception {
        var local = new ThreadLocal<String>();
        var pool = Executors.newSingleThreadExecutor();
        var originalMdc = MDC.getCopyOfContextMap();
        try {
            local.set("request-A"); MDC.put("labRequest", "request-A");
            r.check("threadLocalNotAutomaticallyPropagated", pool.submit(() -> local.get() == null && MDC.get("labRequest") == null).get(5, TimeUnit.SECONDS));
            pool.submit(() -> local.set("stale-request")).get(5, TimeUnit.SECONDS);
            r.check("reusedWorkerRetainsUnclearedValue", pool.submit(() -> "stale-request".equals(local.get())).get(5, TimeUnit.SECONDS));
            pool.submit(local::remove).get(5, TimeUnit.SECONDS);
            String captured = local.get();
            Map<String, String> capturedMdc = MDC.getCopyOfContextMap();
            r.check("explicitSnapshotPropagates", pool.submit(() -> {
                var previous = MDC.getCopyOfContextMap();
                String previousLocal = local.get();
                try { local.set(captured); MDC.setContextMap(capturedMdc); return "request-A".equals(local.get()) && "request-A".equals(MDC.get("labRequest")); }
                finally { if (previousLocal == null) local.remove(); else local.set(previousLocal); restore(previous); }
            }).get(5, TimeUnit.SECONDS));
            r.check("workerContextRestored", pool.submit(() -> local.get() == null && MDC.get("labRequest") == null).get(5, TimeUnit.SECONDS));
            r.step("上下文与清理", "展示线程复用残留；提交时捕获 ThreadLocal/MDC，执行后在 finally 恢复工作线程原上下文。", Map.of("snapshot", captured));
        } finally { local.remove(); restore(originalMdc); stop(pool); }
    }
    private static void restore(Map<String, String> context) { if (context == null) MDC.clear(); else MDC.setContextMap(context); }
    private static void bulkhead(Builder r) throws Exception {
        var slowPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        var healthyPool = Executors.newSingleThreadExecutor();
        var entered = new CountDownLatch(1); var release = new CountDownLatch(1);
        var slowBodyDone = new CountDownLatch(1);
        try {
            var slow = CompletableFuture.supplyAsync(() -> {
                entered.countDown();
                try { await(release); return "late-result"; }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new CompletionException(e); }
                finally { slowBodyDone.countDown(); }
            }, slowPool);
            await(entered);
            var healthyOnSharedPool = slowPool.submit(() -> "healthy");
            try { healthyOnSharedPool.get(50, TimeUnit.MILLISECONDS); throw new AssertionError("Expected shared-pool queue delay"); }
            catch (TimeoutException expected) { r.check("healthyRequestBlockedBySharedPool", !healthyOnSharedPool.isDone()); }
            r.check("separateBulkheadPreservesHealthyDependency", "healthy".equals(healthyPool.submit(() -> "healthy").get(5, TimeUnit.SECONDS)));
            try { slow.orTimeout(50, TimeUnit.MILLISECONDS).get(5, TimeUnit.SECONDS); throw new AssertionError("Expected dependency deadline"); }
            catch (ExecutionException expected) { r.check("deadlineCompletesFutureExceptionally", expected.getCause() instanceof TimeoutException); }
            r.check("futureDeadlineDoesNotStopUnderlyingWork", slowBodyDone.getCount() == 1 && slowPool.getActiveCount() == 1);
            boolean rejected = false;
            try { slowPool.submit(() -> "extra"); } catch (RejectedExecutionException expected) { rejected = true; }
            r.check("saturatedBulkheadRejectsExtraWork", rejected);
            release.countDown(); await(slowBodyDone);
            r.check("lateCompletionCannotReplaceTimeoutResult", slow.isCompletedExceptionally());
            r.check("sharedQueueDrainsAfterRelease", "healthy".equals(healthyOnSharedPool.get(5, TimeUnit.SECONDS)));
            r.step("依赖隔离与超时预算", "慢依赖占满共享池时健康请求排队；独立隔离池仍可完成。CompletableFuture超时只结束结果等待，底层任务继续占资源，需配合客户端超时/协作取消；有界队列在饱和时拒绝新请求，避免无限积压。", Map.of("workersPerPool", 1, "slowQueueCapacity", 1, "deadlineMs", 50, "underlyingWorkContinuedAfterTimeout", true));
        } finally { release.countDown(); try { stop(slowPool); } finally { stop(healthyPool); } }
    }
}
