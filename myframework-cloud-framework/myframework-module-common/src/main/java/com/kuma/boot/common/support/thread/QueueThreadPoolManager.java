/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.thread;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.thread.ThreadUtils;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueThreadPoolManager {
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 0;
    private static final int WORK_QUEUE_SIZE = 50;
    Map<String, Object> cacheMap = new ConcurrentHashMap<String, Object>();
    Queue<Object> msgQueue = new LinkedBlockingQueue<Object>();
    final RejectedExecutionHandler handler = (r, executor) -> {
        this.msgQueue.offer(((BusinessThread)r).getkey());
        LogUtils.info("\u7cfb\u7edf\u4efb\u52a1\u592a\u5fd9\u4e86,\u628a\u6b64\u8ba2\u5355\u4ea4\u7ed9(\u8c03\u5ea6\u7ebf\u7a0b\u6c60)\u9010\u4e00\u5904\u7406\uff0c\u8ba2\u5355\u53f7\uff1a" + ((BusinessThread)r).getkey(), new Object[0]);
    };
    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 10, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50), this.handler);
    final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(5);
    final ScheduledFuture<?> scheduledFuture = this.scheduler.scheduleAtFixedRate(() -> {
        if (!this.msgQueue.isEmpty() && this.threadPool.getQueue().size() < 50) {
            String orderId = (String)this.msgQueue.poll();
            BusinessThread businessThread = new BusinessThread(orderId);
            this.threadPool.execute(businessThread);
            LogUtils.info("(\u8c03\u5ea6\u7ebf\u7a0b\u6c60)\u7f13\u51b2\u961f\u5217\u51fa\u73b0\u8ba2\u5355\u4e1a\u52a1\uff0c\u91cd\u65b0\u6dfb\u52a0\u5230\u7ebf\u7a0b\u6c60\uff0c\u8ba2\u5355\u53f7\uff1a" + orderId, new Object[0]);
        }
    }, 0L, 1L, TimeUnit.SECONDS);

    public void addOrders(String orderId) {
        LogUtils.info("\u6b64\u8ba2\u5355\u51c6\u5907\u6dfb\u52a0\u5230\u7ebf\u7a0b\u6c60\uff0c\u8ba2\u5355\u53f7\uff1a" + orderId, new Object[0]);
        if (this.cacheMap.get(orderId) == null) {
            this.cacheMap.put(orderId, new Object());
            BusinessThread businessThread = new BusinessThread(orderId);
            this.threadPool.execute(businessThread);
        }
    }

    public Queue<Object> getMsgQueue() {
        return this.msgQueue;
    }

    public void shutdown() {
        LogUtils.info("\u7ec8\u6b62\u8ba2\u5355\u7ebf\u7a0b\u6c60+\u8c03\u5ea6\u7ebf\u7a0b\u6c60\uff1a" + this.scheduledFuture.cancel(false), new Object[0]);
        ThreadUtils.shutdownThreadPool(this.scheduler);
        ThreadUtils.shutdownThreadPool(this.threadPool);
    }

    public static class BusinessThread
    implements Runnable {
        private String key;

        public BusinessThread(String key) {
            this.key = key;
        }

        public String getkey() {
            return this.key;
        }

        public void setkey(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            LogUtils.info("\u591a\u7ebf\u7a0b\u5df2\u7ecf\u5904\u7406\u8ba2\u5355\u63d2\u5165\u7cfb\u7edf\uff0c\u8ba2\u5355\u53f7\uff1a" + this.key, new Object[0]);
        }
    }
}

