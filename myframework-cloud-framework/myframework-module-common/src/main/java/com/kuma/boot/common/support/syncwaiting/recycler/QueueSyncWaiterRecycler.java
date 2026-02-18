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

package com.kuma.boot.common.support.syncwaiting.recycler;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 基于 {@link Queue} 实现的 {@link SyncWaiterRecycler}。
 */
public class QueueSyncWaiterRecycler implements SyncWaiterRecycler {

    public QueueSyncWaiterRecycler( Queue<SyncWaiter> queue ) {
        this.queue = queue;
    }

    /**
     * 队列
     */
    protected Queue<SyncWaiter> queue;

    public Queue<SyncWaiter> getQueue() {
        return queue;
    }

    /**
     * 无限制的队列。
     */
    public QueueSyncWaiterRecycler() {
        this(new LinkedList<>());
    }

    /**
     * 固定回收数量的队列。
     *
     * @param n 固定的数量
     */
    public QueueSyncWaiterRecycler( int n ) {
        this(new Limited<>(n));
    }

    @Override
    public void recycle( SyncWaiter waiter ) {
        queue.offer(waiter);
    }

    @Override
    public SyncWaiter reuse() {
        return queue.poll();
    }

    /**
     * Limited
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    public static class Limited<E> extends AbstractQueue<E> {

        private final Queue<E> queue = new LinkedList<>();

        private final int limit;

        public Limited( int limit ) {
            this.limit = limit;
        }

        @Override
        public Iterator<E> iterator() {
            return queue.iterator();
        }

        @Override
        public int size() {
            return queue.size();
        }

        /**
         * 如果到达上限就不再添加到队列中。
         *
         * @param e 元素
         * @return 是否添加成功
         */
        @Override
        public boolean offer( E e ) {
            if (size() < limit) {
                return queue.offer(e);
            }
            return false;
        }

        @Override
        public E poll() {
            return queue.poll();
        }

        @Override
        public E peek() {
            return queue.peek();
        }
    }
}
