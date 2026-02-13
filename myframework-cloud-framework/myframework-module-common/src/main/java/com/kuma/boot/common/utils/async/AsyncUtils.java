/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class AsyncUtils {
    private static final String name = "\u5f02\u6b65\u5904\u7406\u5f02\u5e38\u5de5\u5177";
    private static final ExecutorService executorService = new ThreadPoolExecutor(1, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory(){

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(AsyncUtils.name);
            return t;
        }
    });
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1, new ThreadFactory(){

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });

    private AsyncUtils() {
    }

    public static void execute(Runnable task) {
        AsyncUtils.execute(task, 0, 0, null);
    }

    public static void execute(Runnable task, int maxRetryCount) {
        AsyncUtils.execute(task, maxRetryCount, 0, null);
    }

    public static void execute(Runnable task, int maxRetryCount, BiConsumer<Boolean, Throwable> consumer) {
        AsyncUtils.execute(task, maxRetryCount, 0, consumer);
    }

    public static void execute(Runnable task, int maxRetryCount, int delaySeconds, BiConsumer<Boolean, Throwable> consumer) {
        new Executor(task, maxRetryCount, delaySeconds, consumer).execute();
    }

    public static void executeDelay(Runnable task, int firstDelaySeconds, int maxRetryCount, int delaySeconds, BiConsumer<Boolean, Throwable> consumer) {
        Executor executor = new Executor(task, maxRetryCount, delaySeconds, consumer);
        if (firstDelaySeconds > 0) {
            LogUtils.warn("\u5f02\u6b65\u5904\u7406\u5f02\u5e38\u5de5\u5177 \u5ef6\u8fdf\u6267\u884c\u5f02\u5e38\uff0c\u5c06\u4f1a\u5728[{}]\u79d2\u540e\u6267\u884c\u91cd\u8bd5", firstDelaySeconds);
            SCHEDULED_EXECUTOR_SERVICE.schedule(new Task(executor), (long)firstDelaySeconds * 1000L, TimeUnit.SECONDS);
        } else {
            executor.execute();
        }
    }

    private static class Executor
    implements Runnable {
        private final int[] retrySeconds = new int[]{1, 2, 3, 4, 5};
        private final Runnable task;
        private final int maxRetryCount;
        private final int delaySeconds;
        private int retryAttempts;
        private final BiConsumer<Boolean, Throwable> resultConsumer;

        public Executor(Runnable task, int maxRetryCount, int delaySeconds, BiConsumer<Boolean, Throwable> resultConsumer) {
            this.task = task;
            this.maxRetryCount = maxRetryCount;
            this.delaySeconds = delaySeconds;
            this.resultConsumer = resultConsumer;
        }

        public void execute() {
            executorService.execute(this);
        }

        @Override
        public void run() {
            Throwable exception = null;
            try {
                this.task.run();
            }
            catch (Throwable e) {
                if (this.retryAttempts++ < this.maxRetryCount) {
                    int delay = this.delaySeconds > 0 ? this.delaySeconds : this.retrySeconds[Math.min(this.retryAttempts, this.retrySeconds.length) - 1];
                    LogUtils.warn("\u5f02\u6b65\u5904\u7406\u5f02\u5e38\u5de5\u5177 \u6267\u884c\u5f02\u5e38\uff0c\u5c06\u4f1a\u5728[{}]\u79d2\u540e\u8fdb\u884c\u7b2c[{}]\u6b21\u91cd\u8bd5\uff0c\u5f02\u5e38\u4fe1\u606f\uff1a" + e.getMessage(), delay, this.retryAttempts);
                    SCHEDULED_EXECUTOR_SERVICE.schedule(new Task(this), (long)delay * 1000L, TimeUnit.SECONDS);
                    return;
                }
                exception = e;
            }
            if (exception != null) {
                if (this.maxRetryCount > 0) {
                    LogUtils.error("\u5f02\u6b65\u5904\u7406\u5f02\u5e38\u5de5\u5177 \u6267\u884c\u5f02\u5e38\uff0c\u91cd\u8bd5[{}]\u540e\u4ecd\u7136\u5931\u8d25\uff0c\u5f02\u5e38\u4fe1\u606f\uff1a" + exception.getMessage(), this.maxRetryCount);
                } else {
                    LogUtils.error("\u5f02\u6b65\u5904\u7406\u5f02\u5e38\u5de5\u5177 \u6267\u884c\u5f02\u5e38, \u5f02\u5e38\u4fe1\u606f\uff1a{}", exception.getMessage());
                }
            }
            if (this.resultConsumer != null) {
                try {
                    this.resultConsumer.accept(exception == null, exception);
                }
                catch (Throwable e) {
                    LogUtils.error(e, "\u5f02\u6b65\u5904\u7406\u5f02\u5e38\u5de5\u5177 \u5904\u7406\u7ed3\u679c\u56de\u8c03\u5f02\u5e38", new Object[0]);
                }
            }
        }
    }

    private static class Task
    extends TimerTask {
        private final Executor task;

        public Task(Executor task) {
            this.task = task;
        }

        @Override
        public void run() {
            this.task.execute();
        }
    }
}

