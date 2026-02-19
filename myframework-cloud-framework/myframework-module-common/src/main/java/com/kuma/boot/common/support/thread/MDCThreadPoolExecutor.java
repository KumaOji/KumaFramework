/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.thread;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 这是{@link ThreadPoolTaskExecutor}的一个简单替换，可搭配TransmittableThreadLocal实现父子线程之间的数据传递
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:02:27
 */
public class MDCThreadPoolExecutor extends ThreadPoolExecutor {

    public MDCThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MDCThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public MDCThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public MDCThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        super(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler);
    }

    @Override
    public void execute(Runnable runnable) {
        Runnable ttlRunnable = TtlRunnable.get(runnable);
        showThreadPoolInfo("execute(Runnable task)");
        super.execute(new MDCRunnable(ttlRunnable));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Callable<T> ttlCallable = TtlCallable.get(task);
        showThreadPoolInfo("submit(Callable<T> task)");
        return super.submit(new MDCCallable<>(ttlCallable));
    }

    @Override
    public Future<?> submit(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        showThreadPoolInfo("submit(Runnable task)");
        return super.submit(new MDCRunnable(ttlRunnable));
    }

    /**
     * 每次执行任务时输出当前线程池状态
     * @param method method
     * @since 2021-09-02 20:03:15
     */
    private void showThreadPoolInfo(String method) {
        ThreadFactory threadFactory = this.getThreadFactory();
        ThreadPoolFactory threadPoolFactory = null;
        if (threadFactory instanceof ThreadPoolFactory) {
            threadPoolFactory = (ThreadPoolFactory) threadFactory;
        }

        String threadNamePrefix =
                Objects.nonNull(threadPoolFactory)
                        ? threadPoolFactory.getNamePrefix()
                        : Thread.currentThread().getName();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTrace[stackTrace.length - 2];

        // LogUtils.debug(
        // "className[{}] methodName[{}] lineNumber[{}] threadNamePrefix[{}] method[{}] "
        // + " taskCount[{}] completedTaskCount[{}] activeCount[{}] queueSize[{}]",
        // stackTraceElement.getClassName(),
        // stackTraceElement.getMethodName(),
        // stackTraceElement.getLineNumber(),
        // threadNamePrefix,
        // method,
        // this.getTaskCount(),
        // this.getCompletedTaskCount(),
        // this.getActiveCount(),
        // this.getQueue().size());
    }
}
