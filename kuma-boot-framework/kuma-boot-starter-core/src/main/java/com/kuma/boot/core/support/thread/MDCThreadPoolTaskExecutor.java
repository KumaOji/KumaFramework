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

package com.kuma.boot.core.support.thread;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步线程池任务执行人
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-29 21:59:40
 */
public class MDCThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public void execute(Runnable runnable) {
        Runnable ttlRunnable = TtlRunnable.get(runnable);
        showThreadPoolInfo("execute(Runnable task)");
        super.execute(new MDCRunnable((ttlRunnable)));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Callable<T> ttlCallable = TtlCallable.get(task);
        showThreadPoolInfo("submit(Callable<T> task)");
        return super.submit(new MDCCallable<>((ttlCallable)));
    }

    @Override
    public Future<?> submit(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        showThreadPoolInfo("submit(Runnable task)");
        return super.submit(new MDCRunnable((ttlRunnable)));
    }

    @Override
    public <T> CompletableFuture<T> submitCompletable(Callable<T> task) {
        Callable<T> ttlCallable = TtlCallable.get(task);
        showThreadPoolInfo("submitCompletable(Runnable task)");
        return super.submitCompletable(new MDCCallable<>(ttlCallable));
    }

    @Override
    public CompletableFuture<Void> submitCompletable(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        showThreadPoolInfo("submit(Runnable task)");
        return super.submitCompletable(new MDCRunnable(ttlRunnable));
    }

    /**
     * 每次执行任务时输出当前线程池状态
     * @param method method
     * @since 2021-09-02 20:03:15
     */
    private void showThreadPoolInfo(String method) {
        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTrace[stackTrace.length - 2];

        // LogUtils.info(
        // "className[{}] methodName[{}] lineNumber[{}] threadNamePrefix[{}] method[{}] "
        // + " taskCount[{}] completedTaskCount[{}] activeCount[{}] queueSize[{}]",
        // stackTraceElement.getClassName(),
        // stackTraceElement.getMethodName(),
        // stackTraceElement.getLineNumber(),
        // this.getThreadNamePrefix(),
        // method,
        // threadPoolExecutor.getTaskCount(),
        // threadPoolExecutor.getCompletedTaskCount(),
        // threadPoolExecutor.getActiveCount(),
        // threadPoolExecutor.getQueue().size());
    }
}
