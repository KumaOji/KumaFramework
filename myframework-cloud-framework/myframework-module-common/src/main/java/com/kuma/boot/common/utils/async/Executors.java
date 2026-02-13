/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import com.kuma.boot.common.utils.async.DirectExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class Executors {
    private Executors() {
    }

    public static Executor directExecutor() {
        return DirectExecutor.INSTANCE;
    }

    public static ExecutorService newDirectExecutorService() {
        return new DirectExecutorService(true);
    }

    public static ExecutorService newDirectExecutorServiceWithNoOpShutdown() {
        return new DirectExecutorService(false);
    }

    private static enum DirectExecutor implements Executor
    {
        INSTANCE;


        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}

