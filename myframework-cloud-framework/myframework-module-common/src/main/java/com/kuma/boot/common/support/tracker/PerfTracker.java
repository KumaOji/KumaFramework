/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.date.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.common.support.tracker;

import cn.hutool.core.date.StopWatch;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerfTracker {
    private static final Logger log = LoggerFactory.getLogger(PerfTracker.class);
    private final StopWatch stopWatch = new StopWatch();
    private final String methodName;

    private PerfTracker(String methodName) {
        this.methodName = methodName;
    }

    public static <T> T tracker(Supplier<T> supplier) {
        try (TimerContext timerContext = PerfTracker.start();){
            timerContext.start();
            T result = supplier.get();
            timerContext.stop();
            T t = result;
            return t;
        }
    }

    private static TimerContext start() {
        return new TimerContext(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    private static class TimerContext
    implements AutoCloseable {
        private final PerfTracker tracker;

        private TimerContext(String methodName) {
            this.tracker = new PerfTracker(methodName);
        }

        private void start() {
            this.tracker.stopWatch.start();
        }

        private void stop() {
            this.tracker.stopWatch.stop();
        }

        @Override
        public void close() {
            long executeTime = this.tracker.stopWatch.getTotalTimeMillis();
            if (executeTime > 500L) {
                log.warn("\u6162\u67e5\u8be2\u544a\u8b66\uff1a\u65b9\u6cd5 {} \u8017\u65f6 {}ms", (Object)this.tracker.methodName, (Object)executeTime);
            }
        }
    }
}

