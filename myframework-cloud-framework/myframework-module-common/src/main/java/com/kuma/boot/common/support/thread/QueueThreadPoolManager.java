/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

/**
 * QueueThreadPoolManager
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class QueueThreadPoolManager {

    // 线程池维护线程的最少数量
    private static final int CORE_POOL_SIZE = 2;

    // 线程池维护线程的最大数量
    private static final int MAX_POOL_SIZE = 10;

    // 线程池维护线程所允许的空闲时间
    private static final int KEEP_ALIVE_TIME = 0;

    // 线程池所使用的缓冲队列大小
    private static final int WORK_QUEUE_SIZE = 50;

    /**
     * 用于储存在队列中的订单,防止重复提交,在真实场景中，可用redis代替 验证重复
     */
    Map<String, Object> cacheMap = new ConcurrentHashMap<>();

    /**
     * 订单的缓冲队列,当线程池满了，则将订单存入到此缓冲队列
     */
    Queue<Object> msgQueue = new LinkedBlockingQueue<>();

    /**
     * 当线程池的容量满了，执行下面代码，将订单存入到缓冲队列
     */
    final RejectedExecutionHandler handler =
            ( r, executor ) -> {
                // 订单加入到缓冲队列
                msgQueue.offer(( (BusinessThread) r ).getkey());

                LogUtils.info(
                        "系统任务太忙了,把此订单交给(调度线程池)逐一处理，订单号：" + ( (BusinessThread) r ).getkey());
            };

    /**
     * 创建线程池
     */
    final ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAX_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(WORK_QUEUE_SIZE),
                    this.handler);

    /**
     * 线程池的定时任务----> 称为(调度线程池)。此线程池支持 定时以及周期性执行任务的需求。
     */
    final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(5);

    /**
     * 检查(调度线程池)，每秒执行一次，查看订单的缓冲队列是否有 订单记录，则重新加入到线程池
     */
    final ScheduledFuture<?> scheduledFuture =
            scheduler.scheduleAtFixedRate(
                    () -> {
                        // 判断缓冲队列是否存在记录
                        if (!msgQueue.isEmpty()) {
                            // 当线程池的队列容量少于WORK_QUEUE_SIZE，则开始把缓冲队列的订单 加入到 线程池
                            if (threadPool.getQueue().size() < WORK_QUEUE_SIZE) {
                                String orderId = (String) msgQueue.poll();
                                BusinessThread businessThread = new BusinessThread(orderId);
                                threadPool.execute(businessThread);
                                LogUtils.info("(调度线程池)缓冲队列出现订单业务，重新添加到线程池，订单号：" + orderId);
                            }
                        }
                    },
                    0,
                    1,
                    TimeUnit.SECONDS);

    /**
     * 将任务加入订单线程池
     */
    public void addOrders( String orderId ) {
        LogUtils.info("此订单准备添加到线程池，订单号：" + orderId);

        // 验证当前进入的订单是否已经存在
        if (cacheMap.get(orderId) == null) {
            cacheMap.put(orderId, new Object());

            BusinessThread businessThread = new BusinessThread(orderId);

            threadPool.execute(businessThread);
        }
    }

    /**
     * 获取消息缓冲队列
     */
    public Queue<Object> getMsgQueue() {
        return msgQueue;
    }

    /**
     * 终止订单线程池+调度线程池
     */
    public void shutdown() {
        // true表示如果定时任务在执行，立即中止，false则等待任务结束后再停止
        LogUtils.info("终止订单线程池+调度线程池：" + scheduledFuture.cancel(false));
        ThreadUtils.shutdownThreadPool(scheduler);
        ThreadUtils.shutdownThreadPool(threadPool);
    }

    /**
     * BusinessThread
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class BusinessThread implements Runnable {

        private String key;

        public BusinessThread( String key ) {
            this.key = key;
        }

        public String getkey() {
            return key;
        }

        public void setkey( String key ) {
            this.key = key;
        }

        @Override
        public void run() {
            // 业务操作
            LogUtils.info("多线程已经处理订单插入系统，订单号：" + key);

            // 线程阻塞
            /*
             * try { Thread.sleep(1000); LogUtils.info("多线程已经处理订单插入系统，订单号："+key); } catch
             * (InterruptedException e) { LogUtils.error(e); }
             */
        }
    }
}
