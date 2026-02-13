/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mapqueue.concurrent;

import com.kuma.boot.common.support.mapqueue.concept.MapQueue;
import com.kuma.boot.common.support.mapqueue.concept.MapQueueElement;
import com.kuma.boot.common.support.mapqueue.concurrent.BlockingMapQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractBlockingMapQueue<K, V>
implements BlockingMapQueue<K, V> {
    private final int capacity;
    private int count;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    private final Map<K, V> map;
    private final Map<K, V> readOnly;
    private final List<MapQueue.Synchronizer<K, V>> synchronizers = new CopyOnWriteArrayList<MapQueue.Synchronizer<K, V>>();

    private void invokeSynchronizersBeforeEnqueue(K key, V value) {
        this.synchronizers.forEach((? super T it) -> it.beforeEnqueue(key, value, this.readOnly));
    }

    private void invokeSynchronizersAfterEnqueue(K key, V value) {
        this.synchronizers.forEach((? super T it) -> it.afterEnqueue(key, value, this.readOnly));
    }

    private void invokeSynchronizersBeforeDequeue(K key, V value) {
        this.synchronizers.forEach((? super T it) -> it.beforeDequeue(key, value, this.readOnly));
    }

    private void invokeSynchronizersAfterDequeue(K key, V value) {
        this.synchronizers.forEach((? super T it) -> it.afterDequeue(key, value, this.readOnly));
    }

    private boolean nonBlockingEnqueue(K k, V v) {
        boolean x;
        if (this.map.containsKey(k)) {
            this.invokeSynchronizersBeforeEnqueue(k, v);
            this.map.put(k, v);
            x = true;
        } else if (this.count < this.capacity) {
            this.invokeSynchronizersBeforeEnqueue(k, v);
            this.map.put(k, v);
            ++this.count;
            x = true;
        } else {
            x = false;
        }
        if (x) {
            this.invokeSynchronizersAfterEnqueue(k, v);
        }
        this.notEmpty.signal();
        return x;
    }

    private V blockingEnqueue(K k, V v) throws InterruptedException {
        V x;
        if (this.map.containsKey(k)) {
            this.invokeSynchronizersBeforeEnqueue(k, v);
            x = this.map.put(k, v);
        } else {
            while (this.count == this.capacity) {
                this.notFull.await();
            }
            this.invokeSynchronizersBeforeEnqueue(k, v);
            x = this.map.put(k, v);
            ++this.count;
        }
        this.invokeSynchronizersAfterEnqueue(k, v);
        this.notEmpty.signal();
        return x;
    }

    private boolean blockingEnqueue(K k, V v, long timeout, TimeUnit unit) throws InterruptedException {
        if (this.map.containsKey(k)) {
            this.invokeSynchronizersBeforeEnqueue(k, v);
            this.map.put(k, v);
        } else {
            long nanos = unit.toNanos(timeout);
            while (this.count == this.capacity) {
                if (nanos <= 0L) {
                    return false;
                }
                nanos = this.notFull.awaitNanos(nanos);
            }
            this.invokeSynchronizersBeforeEnqueue(k, v);
            this.map.put(k, v);
            ++this.count;
        }
        this.invokeSynchronizersAfterEnqueue(k, v);
        this.notEmpty.signal();
        return true;
    }

    private V nonBlockingDequeue(K k) {
        if (this.map.containsKey(k)) {
            this.invokeSynchronizersBeforeDequeue(k, this.map.get(k));
            V v = this.map.remove(k);
            --this.count;
            this.invokeSynchronizersAfterDequeue(k, v);
            this.notFull.signal();
            return v;
        }
        return null;
    }

    private Map.Entry<K, V> nonBlockingDequeue() {
        if (this.count > 0) {
            return this.dequeue0();
        }
        return null;
    }

    private Map.Entry<K, V> blockingDequeue() throws InterruptedException {
        while (this.count == 0) {
            this.notEmpty.await();
        }
        return this.dequeue0();
    }

    private Map.Entry<K, V> blockingDequeue(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        while (this.count == 0) {
            if (nanos <= 0L) {
                return null;
            }
            nanos = this.notEmpty.awaitNanos(nanos);
        }
        return this.dequeue0();
    }

    private Map.Entry<K, V> dequeue0() {
        Iterator<Map.Entry<K, V>> iterator = this.map.entrySet().iterator();
        Map.Entry<K, V> entry = iterator.next();
        K k = entry.getKey();
        V v = entry.getValue();
        this.invokeSynchronizersBeforeDequeue(k, v);
        iterator.remove();
        --this.count;
        this.invokeSynchronizersAfterDequeue(k, v);
        this.notFull.signal();
        return entry;
    }

    protected abstract Map<K, V> createMap();

    public AbstractBlockingMapQueue() {
        this(Integer.MAX_VALUE, false);
    }

    public AbstractBlockingMapQueue(int capacity) {
        this(capacity, false);
    }

    public AbstractBlockingMapQueue(boolean fair) {
        this(Integer.MAX_VALUE, fair);
    }

    public AbstractBlockingMapQueue(Map<K, V> map) {
        this(Integer.MAX_VALUE, false, map);
    }

    public AbstractBlockingMapQueue(int capacity, Map<K, V> map) {
        this(capacity, false, map);
    }

    public AbstractBlockingMapQueue(boolean fair, Map<K, V> map) {
        this(Integer.MAX_VALUE, fair, map);
    }

    public AbstractBlockingMapQueue(int capacity, boolean fair) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        this.map = this.createMap();
        this.readOnly = Collections.unmodifiableMap(this.map);
        this.lock = new ReentrantLock(fair);
        this.notEmpty = this.lock.newCondition();
        this.notFull = this.lock.newCondition();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractBlockingMapQueue(int capacity, boolean fair, Map<? extends K, ? extends V> map) {
        this(capacity, fair);
        this.lock.lock();
        try {
            this.map.putAll(map);
            int size = map.size();
            if (size >= capacity) {
                throw new IllegalStateException("Queue full");
            }
            this.count = size;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void addSynchronizer(MapQueue.Synchronizer<K, V> synchronizer) {
        this.synchronizers.add(synchronizer);
    }

    @Override
    public void removeSynchronizer(MapQueue.Synchronizer<K, V> synchronizer) {
        this.synchronizers.remove(synchronizer);
    }

    public int size() {
        return this.count;
    }

    public int remainingCapacity() {
        return this.capacity - this.count;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V put(K key, V value) throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            V v = this.blockingEnqueue(key, value);
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void putAll(Map<? extends K, ? extends V> m) throws InterruptedException {
        for (Map.Entry<K, V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V putIfAbsent(K key, V value) throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            V oldValue = this.map.get(key);
            if (oldValue == null) {
                V v = this.blockingEnqueue(key, value);
                return v;
            }
            V v = oldValue;
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) throws InterruptedException {
        Objects.requireNonNull(mappingFunction);
        this.lock.lockInterruptibly();
        try {
            V oldValue = this.map.get(key);
            if (oldValue == null) {
                V newValue = mappingFunction.apply(key);
                if (newValue == null) {
                    V v = null;
                    return v;
                }
                this.blockingEnqueue(key, newValue);
                V v = newValue;
                return v;
            }
            V v = oldValue;
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) throws InterruptedException {
        Objects.requireNonNull(remappingFunction);
        this.lock.lockInterruptibly();
        try {
            V oldValue = this.map.get(key);
            if (oldValue != null) {
                V newValue = remappingFunction.apply(key, oldValue);
                if (newValue == null) {
                    this.nonBlockingDequeue(key);
                    V v = null;
                    return v;
                }
                this.blockingEnqueue(key, newValue);
                V v = newValue;
                return v;
            }
            V v = null;
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) throws InterruptedException {
        Objects.requireNonNull(remappingFunction);
        this.lock.lockInterruptibly();
        try {
            Objects.requireNonNull(remappingFunction);
            V oldValue = this.map.get(key);
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue == null) {
                if (oldValue != null || this.map.containsKey(key)) {
                    this.nonBlockingDequeue(key);
                }
                V v = null;
                return v;
            }
            this.blockingEnqueue(key, newValue);
            V v = newValue;
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) throws InterruptedException {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        this.lock.lockInterruptibly();
        try {
            V newValue;
            V oldValue = this.get(key);
            V v = newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
            if (newValue == null) {
                this.nonBlockingDequeue(key);
            } else {
                this.blockingEnqueue(key, newValue);
            }
            V v2 = newValue;
            return v2;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean offer(K key, V value, long timeout, TimeUnit unit) throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            boolean bl = this.blockingEnqueue(key, value, timeout, unit);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean offer(K key, V value) {
        this.lock.lock();
        try {
            boolean bl = this.nonBlockingEnqueue(key, value);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public Map.Entry<K, V> take() throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            Map.Entry<K, V> entry = this.blockingDequeue();
            return entry;
        }
        finally {
            this.lock.unlock();
        }
    }

    public V takeValue() throws InterruptedException {
        return this.take().getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map.Entry<K, V> poll(long timeout, TimeUnit unit) throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            Map.Entry<K, V> entry = this.blockingDequeue(timeout, unit);
            return entry;
        }
        finally {
            this.lock.unlock();
        }
    }

    public V pollValue(long timeout, TimeUnit unit) throws InterruptedException {
        return this.poll(timeout, unit).getValue();
    }

    public Map.Entry<K, V> poll() {
        this.lock.lock();
        try {
            Map.Entry<K, V> entry = this.nonBlockingDequeue();
            return entry;
        }
        finally {
            this.lock.unlock();
        }
    }

    public V pollValue() {
        return this.poll().getValue();
    }

    public Map.Entry<K, V> peek() {
        this.lock.lock();
        try {
            if (this.count == 0) {
                Map.Entry<K, V> entry = null;
                return entry;
            }
            Iterator<Map.Entry<K, V>> iterator = this.map.entrySet().iterator();
            if (iterator.hasNext()) {
                Map.Entry<K, V> entry = iterator.next();
                return entry;
            }
            Map.Entry<K, V> entry = null;
            return entry;
        }
        finally {
            this.lock.unlock();
        }
    }

    public V peekValue() {
        return this.peek().getValue();
    }

    public V get(K key) {
        this.lock.lock();
        try {
            V v = this.map.get(key);
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V getOrDefault(K key, V defaultValue) {
        this.lock.lock();
        try {
            V v = this.map.getOrDefault(key, defaultValue);
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    public V remove(K key) {
        this.lock.lock();
        try {
            V v = this.nonBlockingDequeue(key);
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(K key, V value) {
        this.lock.lock();
        try {
            boolean remove;
            if (Objects.equals(this.map.get(key), value)) {
                this.invokeSynchronizersBeforeDequeue(key, value);
            }
            if (remove = this.map.remove(key, value)) {
                --this.count;
                this.invokeSynchronizersAfterDequeue(key, value);
                this.notFull.signal();
            }
            boolean bl = remove;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeValue(V value) {
        this.lock.lock();
        try {
            boolean removed = false;
            Iterator<Map.Entry<K, V>> iterator = this.map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, V> entry = iterator.next();
                K k = entry.getKey();
                V v = entry.getValue();
                if (!Objects.equals(value, v)) continue;
                this.invokeSynchronizersBeforeDequeue(k, v);
                iterator.remove();
                --this.count;
                removed = true;
                this.invokeSynchronizersAfterDequeue(k, v);
            }
            if (removed) {
                this.notFull.signal();
            }
            boolean bl = removed;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean replace(K key, V oldValue, V newValue) {
        this.lock.lock();
        try {
            boolean bl = this.map.replace(key, oldValue, newValue);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V replace(K key, V value) {
        this.lock.lock();
        try {
            V v = this.map.replace(key, value);
            return v;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.lock.lock();
        try {
            this.map.replaceAll(function);
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean containsKey(K key) {
        this.lock.lock();
        try {
            boolean bl = this.map.containsKey(key);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean containsValue(V v) {
        this.lock.lock();
        try {
            boolean bl = this.map.containsValue(v);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public Set<K> keySet() {
        this.lock.lock();
        try {
            Set<K> set = this.map.keySet();
            return set;
        }
        finally {
            this.lock.unlock();
        }
    }

    public Collection<V> values() {
        this.lock.lock();
        try {
            Collection<V> collection = this.map.values();
            return collection;
        }
        finally {
            this.lock.unlock();
        }
    }

    public Set<Map.Entry<K, V>> entrySet() {
        this.lock.lock();
        try {
            Set<Map.Entry<K, V>> set = this.map.entrySet();
            return set;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toArray() {
        this.lock.lock();
        try {
            int size = this.count;
            Object[] a = new Object[size];
            int k = 0;
            for (Map.Entry<K, V> value : this.map.entrySet()) {
                a[k++] = value;
            }
            Object[] objectArray = a;
            return objectArray;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toValueArray() {
        this.lock.lock();
        try {
            int size = this.count;
            Object[] a = new Object[size];
            int k = 0;
            for (V value : this.map.values()) {
                a[k++] = value;
            }
            Object[] objectArray = a;
            return objectArray;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T[] toValueArray(T[] a) {
        this.lock.lock();
        try {
            int size = this.count;
            if (a.length < size) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
            }
            int k = 0;
            for (V value : this.map.values()) {
                a[k++] = value;
            }
            if (a.length > k) {
                a[k] = null;
            }
            Object[] objectArray = a;
            return objectArray;
        }
        finally {
            this.lock.unlock();
        }
    }

    public int hashCode() {
        this.lock.lock();
        try {
            int n = this.map.hashCode();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    public String toString() {
        this.lock.lock();
        try {
            String string = this.map.toString();
            return string;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean equals(Object obj) {
        this.lock.lock();
        try {
            boolean bl = this.map.equals(obj);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void clear() {
        this.lock.lock();
        try {
            this.map.clear();
            this.notFull.signalAll();
        }
        finally {
            this.lock.unlock();
        }
    }

    public int drainValueTo(Collection<? super V> c) {
        return this.drainValueTo(c, Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int drainValueTo(Collection<? super V> c, int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (maxElements <= 0) {
            return 0;
        }
        this.lock.lock();
        try {
            int n;
            block12: {
                boolean signalNotFull;
                int i;
                int n2 = Math.min(maxElements, this.count);
                try {
                    Iterator<V> iterator = this.map.values().iterator();
                    for (i = 0; i < n2 && iterator.hasNext(); ++i) {
                        V next = iterator.next();
                        iterator.remove();
                        c.add(next);
                    }
                    n = n2;
                    if (i <= 0) break block12;
                    signalNotFull = this.count == this.capacity;
                }
                catch (Throwable throwable) {
                    if (i > 0) {
                        boolean signalNotFull2 = this.count == this.capacity;
                        this.count -= i;
                        if (signalNotFull2) {
                            this.notFull.signal();
                        }
                    }
                    throw throwable;
                }
                this.count -= i;
                if (signalNotFull) {
                    this.notFull.signal();
                }
            }
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.lock.lock();
        try {
            this.map.forEach(action);
        }
        finally {
            this.lock.unlock();
        }
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return new Itr(this);
    }

    public Iterator<V> valueIterator() {
        return new ValueItr(this);
    }

    public Spliterator<Map.Entry<K, V>> spliterator() {
        return new LBQSpliterator(this, this);
    }

    public Spliterator<V> valueSpliterator() {
        return new ValueLBQSpliterator(this, this);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        this.lock.lock();
        try {
            s.defaultWriteObject();
            s.writeObject(this.map);
        }
        finally {
            this.lock.unlock();
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Map map = (Map)s.readObject();
        this.map.putAll(map);
        this.count = map.size();
    }

    @Override
    public ConcurrentMap<K, V> map() {
        return new MapImpl(this);
    }

    @Override
    public BlockingQueue<V> queue() {
        return new QueueImpl(this);
    }

    private class Itr
    implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Map.Entry<K, V>> iterator;
        final /* synthetic */ AbstractBlockingMapQueue this$0;

        Itr(AbstractBlockingMapQueue abstractBlockingMapQueue) {
            AbstractBlockingMapQueue abstractBlockingMapQueue2 = abstractBlockingMapQueue;
            Objects.requireNonNull(abstractBlockingMapQueue2);
            this.this$0 = abstractBlockingMapQueue2;
            abstractBlockingMapQueue.lock.lock();
            try {
                this.iterator = abstractBlockingMapQueue.map.entrySet().iterator();
            }
            finally {
                abstractBlockingMapQueue.lock.unlock();
            }
        }

        @Override
        public boolean hasNext() {
            this.this$0.lock.lock();
            try {
                boolean bl = this.iterator.hasNext();
                return bl;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public Map.Entry<K, V> next() {
            this.this$0.lock.lock();
            try {
                Map.Entry entry = this.iterator.next();
                return entry;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public void remove() {
            this.this$0.lock.lock();
            try {
                this.iterator.remove();
            }
            finally {
                this.this$0.lock.unlock();
            }
        }
    }

    private class ValueItr
    implements Iterator<V> {
        private final Iterator<V> iterator;
        final /* synthetic */ AbstractBlockingMapQueue this$0;

        ValueItr(AbstractBlockingMapQueue abstractBlockingMapQueue) {
            AbstractBlockingMapQueue abstractBlockingMapQueue2 = abstractBlockingMapQueue;
            Objects.requireNonNull(abstractBlockingMapQueue2);
            this.this$0 = abstractBlockingMapQueue2;
            abstractBlockingMapQueue.lock.lock();
            try {
                this.iterator = abstractBlockingMapQueue.map.values().iterator();
            }
            finally {
                abstractBlockingMapQueue.lock.unlock();
            }
        }

        @Override
        public boolean hasNext() {
            this.this$0.lock.lock();
            try {
                boolean bl = this.iterator.hasNext();
                return bl;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public V next() {
            this.this$0.lock.lock();
            try {
                Object v = this.iterator.next();
                return v;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public void remove() {
            this.this$0.lock.lock();
            try {
                this.iterator.remove();
            }
            finally {
                this.this$0.lock.unlock();
            }
        }
    }

    private class LBQSpliterator<K, V>
    implements Spliterator<Map.Entry<K, V>> {
        final Spliterator<Map.Entry<K, V>> spliterator;
        final /* synthetic */ AbstractBlockingMapQueue this$0;

        LBQSpliterator(AbstractBlockingMapQueue abstractBlockingMapQueue, AbstractBlockingMapQueue<K, V> queue) {
            AbstractBlockingMapQueue abstractBlockingMapQueue2 = abstractBlockingMapQueue;
            Objects.requireNonNull(abstractBlockingMapQueue2);
            this.this$0 = abstractBlockingMapQueue2;
            this.spliterator = queue.map.entrySet().spliterator();
        }

        @Override
        public long estimateSize() {
            return this.spliterator.estimateSize();
        }

        @Override
        public Spliterator<Map.Entry<K, V>> trySplit() {
            this.this$0.lock.lock();
            try {
                Spliterator<Map.Entry<K, V>> spliterator = this.spliterator.trySplit();
                return spliterator;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            this.this$0.lock.lock();
            try {
                this.spliterator.forEachRemaining(action);
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
            this.this$0.lock.lock();
            try {
                boolean bl = this.spliterator.tryAdvance(action);
                return bl;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public int characteristics() {
            return 4368;
        }
    }

    private class ValueLBQSpliterator<V>
    implements Spliterator<V> {
        final Spliterator<V> spliterator;
        final /* synthetic */ AbstractBlockingMapQueue this$0;

        ValueLBQSpliterator(AbstractBlockingMapQueue abstractBlockingMapQueue, AbstractBlockingMapQueue<K, V> queue) {
            AbstractBlockingMapQueue abstractBlockingMapQueue2 = abstractBlockingMapQueue;
            Objects.requireNonNull(abstractBlockingMapQueue2);
            this.this$0 = abstractBlockingMapQueue2;
            this.spliterator = queue.map.values().spliterator();
        }

        @Override
        public long estimateSize() {
            return this.spliterator.estimateSize();
        }

        @Override
        public Spliterator<V> trySplit() {
            this.this$0.lock.lock();
            try {
                Spliterator<V> spliterator = this.spliterator.trySplit();
                return spliterator;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super V> action) {
            this.this$0.lock.lock();
            try {
                this.spliterator.forEachRemaining(action);
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super V> action) {
            this.this$0.lock.lock();
            try {
                boolean bl = this.spliterator.tryAdvance(action);
                return bl;
            }
            finally {
                this.this$0.lock.unlock();
            }
        }

        @Override
        public int characteristics() {
            return 4368;
        }
    }

    private class MapImpl
    implements ConcurrentMap<K, V> {
        final /* synthetic */ AbstractBlockingMapQueue this$0;

        private MapImpl(AbstractBlockingMapQueue abstractBlockingMapQueue) {
            AbstractBlockingMapQueue abstractBlockingMapQueue2 = abstractBlockingMapQueue;
            Objects.requireNonNull(abstractBlockingMapQueue2);
            this.this$0 = abstractBlockingMapQueue2;
        }

        @Override
        public int size() {
            return this.this$0.size();
        }

        @Override
        public boolean isEmpty() {
            return this.this$0.isEmpty();
        }

        @Override
        public V get(Object key) {
            return this.this$0.get(key);
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return this.this$0.getOrDefault(key, defaultValue);
        }

        @Override
        public boolean containsKey(Object key) {
            return this.this$0.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.this$0.containsValue(value);
        }

        @Override
        public V put(K key, V value) {
            try {
                return this.this$0.put(key, value);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            try {
                this.this$0.putAll(m);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public V putIfAbsent(K key, V value) {
            try {
                return this.this$0.putIfAbsent(key, value);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public V remove(Object key) {
            return this.this$0.remove(key);
        }

        @Override
        public void clear() {
            this.this$0.clear();
        }

        @Override
        public Set<K> keySet() {
            return this.this$0.keySet();
        }

        @Override
        public Collection<V> values() {
            return this.this$0.values();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return this.this$0.entrySet();
        }

        @Override
        public int hashCode() {
            return this.this$0.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return this.this$0.equals(o);
        }

        @Override
        public boolean remove(Object key, Object value) {
            return this.this$0.remove(key, value);
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            return this.this$0.replace(key, oldValue, newValue);
        }

        @Override
        public V replace(K key, V value) {
            return this.this$0.replace(key, value);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            this.this$0.replaceAll(function);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            this.this$0.forEach(action);
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            try {
                return this.this$0.computeIfAbsent(key, mappingFunction);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            try {
                return this.this$0.computeIfPresent(key, remappingFunction);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            try {
                return this.this$0.compute(key, remappingFunction);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            try {
                return this.this$0.merge(key, value, remappingFunction);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public String toString() {
            return this.this$0.toString();
        }
    }

    private class QueueImpl
    extends AbstractQueue<V>
    implements BlockingQueue<V> {
        final /* synthetic */ AbstractBlockingMapQueue this$0;

        private QueueImpl(AbstractBlockingMapQueue abstractBlockingMapQueue) {
            AbstractBlockingMapQueue abstractBlockingMapQueue2 = abstractBlockingMapQueue;
            Objects.requireNonNull(abstractBlockingMapQueue2);
            this.this$0 = abstractBlockingMapQueue2;
        }

        @Override
        public int size() {
            return this.this$0.size();
        }

        @Override
        public int remainingCapacity() {
            return this.this$0.remainingCapacity();
        }

        @Override
        public void put(V v) throws InterruptedException {
            if (v instanceof MapQueueElement) {
                Object key = ((MapQueueElement)v).getKey();
                this.this$0.put(key, v);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean offer(V v) {
            if (v instanceof MapQueueElement) {
                Object key = ((MapQueueElement)v).getKey();
                return this.this$0.offer(key, v);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean offer(V v, long timeout, TimeUnit unit) throws InterruptedException {
            if (v instanceof MapQueueElement) {
                Object key = ((MapQueueElement)v).getKey();
                return this.this$0.offer(key, v, timeout, unit);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public V take() throws InterruptedException {
            return this.this$0.takeValue();
        }

        @Override
        public V poll() {
            return this.this$0.pollValue();
        }

        @Override
        public V poll(long timeout, TimeUnit unit) throws InterruptedException {
            return this.this$0.pollValue(timeout, unit);
        }

        @Override
        public V peek() {
            return this.this$0.peekValue();
        }

        @Override
        public boolean remove(Object o) {
            return this.this$0.removeValue(o);
        }

        @Override
        public boolean contains(Object o) {
            return this.this$0.containsValue(o);
        }

        @Override
        public Object[] toArray() {
            return this.this$0.toValueArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.this$0.toValueArray(a);
        }

        @Override
        public String toString() {
            return this.this$0.toString();
        }

        @Override
        public void clear() {
            this.this$0.clear();
        }

        @Override
        public int drainTo(Collection<? super V> c) {
            return this.this$0.drainValueTo(c);
        }

        @Override
        public int drainTo(Collection<? super V> c, int maxElements) {
            return this.this$0.drainValueTo(c, maxElements);
        }

        @Override
        public Iterator<V> iterator() {
            return this.this$0.valueIterator();
        }

        @Override
        public Spliterator<V> spliterator() {
            return this.this$0.valueSpliterator();
        }
    }
}

