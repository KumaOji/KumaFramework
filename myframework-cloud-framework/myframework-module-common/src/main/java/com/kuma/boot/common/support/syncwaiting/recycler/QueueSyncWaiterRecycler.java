/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.syncwaiting.recycler;

import com.kuma.boot.common.support.syncwaiting.concept.SyncWaiter;
import com.kuma.boot.common.support.syncwaiting.recycler.SyncWaiterRecycler;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class QueueSyncWaiterRecycler
implements SyncWaiterRecycler {
    protected Queue<SyncWaiter> queue;

    public QueueSyncWaiterRecycler(Queue<SyncWaiter> queue) {
        this.queue = queue;
    }

    public Queue<SyncWaiter> getQueue() {
        return this.queue;
    }

    public QueueSyncWaiterRecycler() {
        this(new LinkedList<SyncWaiter>());
    }

    public QueueSyncWaiterRecycler(int n) {
        this(new Limited<SyncWaiter>(n));
    }

    @Override
    public void recycle(SyncWaiter waiter) {
        this.queue.offer(waiter);
    }

    @Override
    public SyncWaiter reuse() {
        return this.queue.poll();
    }

    public static class Limited<E>
    extends AbstractQueue<E> {
        private final Queue<E> queue = new LinkedList();
        private final int limit;

        public Limited(int limit) {
            this.limit = limit;
        }

        @Override
        public Iterator<E> iterator() {
            return this.queue.iterator();
        }

        @Override
        public int size() {
            return this.queue.size();
        }

        @Override
        public boolean offer(E e) {
            if (this.size() < this.limit) {
                return this.queue.offer(e);
            }
            return false;
        }

        @Override
        public E poll() {
            return this.queue.poll();
        }

        @Override
        public E peek() {
            return this.queue.peek();
        }
    }
}

