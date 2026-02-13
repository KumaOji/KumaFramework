/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.mapqueue.concurrent;

import com.kuma.boot.common.support.mapqueue.concept.MapQueueElement;
import com.kuma.boot.common.support.mapqueue.concurrent.BlockingMapQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated
public abstract class AbstractBlockingMapQueueTemp<K, V>
implements BlockingMapQueue<K, V> {
    private final int capacity;
    private final AtomicInteger count = new AtomicInteger();
    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = this.takeLock.newCondition();
    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notFull = this.putLock.newCondition();
    private final Map<K, V> map;

    private void signalNotEmpty() {
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            this.notEmpty.signal();
        }
        finally {
            takeLock.unlock();
        }
    }

    private void signalNotFull() {
        ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            this.notFull.signal();
        }
        finally {
            putLock.unlock();
        }
    }

    @Deprecated
    private Enqueued<V> enqueue(K k, V v) {
        if (this.map.containsKey(k)) {
            return new Enqueued<V>(false, this.map.put(k, v));
        }
        return new Enqueued<V>(true, this.map.put(k, v));
    }

    private Map.Entry<K, V> dequeue() {
        Iterator<Map.Entry<K, V>> iterator = this.map.entrySet().iterator();
        Map.Entry<K, V> entry = iterator.next();
        iterator.remove();
        return entry;
    }

    void fullyLock() {
        this.putLock.lock();
        this.takeLock.lock();
    }

    void fullyUnlock() {
        this.takeLock.unlock();
        this.putLock.unlock();
    }

    void fullyLockInterruptibly() throws InterruptedException {
        this.takeLock.lockInterruptibly();
        this.putLock.lockInterruptibly();
    }

    public AbstractBlockingMapQueueTemp(Map<K, V> map) {
        this(map, Integer.MAX_VALUE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractBlockingMapQueueTemp(Map<K, V> map, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        ReentrantLock putLock = this.putLock;
        putLock.lock();
        this.map = map;
        this.capacity = capacity;
        try {
            int size = map.size();
            if (size >= capacity) {
                throw new IllegalStateException("Queue full");
            }
            this.count.set(size);
        }
        finally {
            putLock.unlock();
        }
    }

    public int size() {
        return this.count.get();
    }

    public int remainingCapacity() {
        return this.capacity - this.count.get();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V put(K k, V v) throws InterruptedException {
        int c;
        V x;
        ReentrantLock putLock = this.putLock;
        AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            if (this.map.containsKey(k)) {
                x = this.map.put(k, v);
                c = count.get();
            } else {
                while (count.get() == this.capacity) {
                    this.notFull.await();
                }
                x = this.map.put(k, v);
                c = count.getAndIncrement();
            }
            if (c + 1 < this.capacity) {
                this.notFull.signal();
            }
        }
        finally {
            putLock.unlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        return x;
    }

    public void putAll(Map<? extends K, ? extends V> m) throws InterruptedException {
        for (Map.Entry<K, V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V putIfAbsent(K k, V v) throws InterruptedException {
        V x;
        int c;
        ReentrantLock putLock = this.putLock;
        AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            V oldValue = this.map.get(k);
            if (oldValue != null) {
                c = count.get();
                x = oldValue;
            } else {
                while (count.get() == this.capacity) {
                    this.notFull.await();
                }
                x = this.map.put(k, v);
                c = count.getAndIncrement();
                if (c + 1 < this.capacity) {
                    this.notFull.signal();
                }
            }
        }
        finally {
            putLock.unlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        return x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) throws InterruptedException {
        V x;
        int c;
        Objects.requireNonNull(mappingFunction);
        ReentrantLock putLock = this.putLock;
        AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            V oldValue = this.map.get(key);
            if (oldValue == null) {
                V newValue = mappingFunction.apply(key);
                if (newValue != null) {
                    while (count.get() == this.capacity) {
                        this.notFull.await();
                    }
                    this.map.put(key, newValue);
                    c = count.getAndIncrement();
                    x = newValue;
                    if (c + 1 < this.capacity) {
                        this.notFull.signal();
                    }
                } else {
                    c = count.get();
                    x = null;
                }
            } else {
                c = count.get();
                x = oldValue;
            }
        }
        finally {
            putLock.unlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        return x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) throws InterruptedException {
        Object x;
        int c;
        Objects.requireNonNull(remappingFunction);
        AtomicInteger count = this.count;
        this.fullyLockInterruptibly();
        try {
            V oldValue = this.map.get(key);
            if (oldValue != null) {
                V newValue = remappingFunction.apply(key, oldValue);
                if (newValue != null) {
                    while (count.get() == this.capacity) {
                        this.notFull.await();
                    }
                    this.map.put(key, newValue);
                    c = count.getAndIncrement();
                    x = newValue;
                    if (c + 1 < this.capacity) {
                        this.notFull.signal();
                    }
                } else {
                    while (count.get() == 0) {
                        this.notEmpty.await();
                    }
                    this.map.remove(key);
                    c = count.getAndDecrement();
                    if (c > 1) {
                        this.notEmpty.signal();
                    }
                    x = null;
                }
            } else {
                c = count.get();
                x = null;
            }
        }
        finally {
            this.fullyUnlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) throws InterruptedException {
        V x;
        int c;
        Objects.requireNonNull(remappingFunction);
        AtomicInteger count = this.count;
        this.fullyLockInterruptibly();
        try {
            Objects.requireNonNull(remappingFunction);
            V oldValue = this.map.get(key);
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue == null) {
                if (oldValue != null || this.map.containsKey(key)) {
                    while (count.get() == 0) {
                        this.notEmpty.await();
                    }
                    this.map.remove(key);
                    c = count.getAndDecrement();
                    if (c > 1) {
                        this.notEmpty.signal();
                    }
                    x = null;
                } else {
                    x = null;
                    c = count.get();
                }
            } else {
                while (count.get() == this.capacity) {
                    this.notFull.await();
                }
                this.map.put(key, newValue);
                c = count.getAndIncrement();
                x = newValue;
                if (c + 1 < this.capacity) {
                    this.notFull.signal();
                }
            }
        }
        finally {
            this.fullyUnlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) throws InterruptedException {
        int c;
        V newValue;
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        AtomicInteger count = this.count;
        this.fullyLockInterruptibly();
        try {
            V oldValue = this.get(key);
            V v = newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
            if (newValue == null) {
                while (count.get() == 0) {
                    this.notEmpty.await();
                }
                this.map.remove(key);
                c = count.getAndDecrement();
                if (c > 1) {
                    this.notEmpty.signal();
                }
            } else {
                while (count.get() == this.capacity) {
                    this.notFull.await();
                }
                this.map.put(key, newValue);
                c = count.getAndIncrement();
                if (c + 1 < this.capacity) {
                    this.notFull.signal();
                }
            }
        }
        finally {
            this.fullyUnlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return newValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean offer(K k, V v, long timeout, TimeUnit unit) throws InterruptedException {
        int c;
        long nanos = unit.toNanos(timeout);
        ReentrantLock putLock = this.putLock;
        AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() == this.capacity) {
                if (nanos <= 0L) {
                    boolean bl = false;
                    return bl;
                }
                nanos = this.notFull.awaitNanos(nanos);
            }
            if (this.map.containsKey(k)) {
                this.map.put(k, v);
                c = count.get();
            } else {
                this.map.put(k, v);
                c = count.getAndIncrement();
            }
            if (c + 1 < this.capacity) {
                this.notFull.signal();
            }
        }
        finally {
            putLock.unlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean offer(K k, V v) {
        AtomicInteger count = this.count;
        if (count.get() == this.capacity) {
            return false;
        }
        int c = -1;
        ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            if (count.get() < this.capacity) {
                if (this.map.containsKey(k)) {
                    this.map.put(k, v);
                    c = count.get();
                } else {
                    this.map.put(k, v);
                    c = count.getAndIncrement();
                }
                if (c + 1 < this.capacity) {
                    this.notFull.signal();
                }
            }
        }
        finally {
            putLock.unlock();
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        return c >= 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map.Entry<K, V> take() throws InterruptedException {
        int c;
        Map.Entry<K, V> x;
        AtomicInteger count = this.count;
        ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                this.notEmpty.await();
            }
            x = this.dequeue();
            c = count.getAndDecrement();
            if (c > 1) {
                this.notEmpty.signal();
            }
        }
        finally {
            takeLock.unlock();
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }

    public V takeValue() throws InterruptedException {
        return this.take().getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map.Entry<K, V> poll(long timeout, TimeUnit unit) throws InterruptedException {
        int c;
        Map.Entry<K, V> x;
        long nanos = unit.toNanos(timeout);
        AtomicInteger count = this.count;
        ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                if (nanos <= 0L) {
                    Map.Entry<K, V> entry = null;
                    return entry;
                }
                nanos = this.notEmpty.awaitNanos(nanos);
            }
            x = this.dequeue();
            c = count.getAndDecrement();
            if (c > 1) {
                this.notEmpty.signal();
            }
        }
        finally {
            takeLock.unlock();
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }

    public V pollValue(long timeout, TimeUnit unit) throws InterruptedException {
        return this.poll(timeout, unit).getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map.Entry<K, V> poll() {
        AtomicInteger count = this.count;
        if (count.get() == 0) {
            return null;
        }
        Map.Entry<K, V> x = null;
        int c = -1;
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            if (count.get() > 0) {
                x = this.dequeue();
                c = count.getAndDecrement();
                if (c > 1) {
                    this.notEmpty.signal();
                }
            }
        }
        finally {
            takeLock.unlock();
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }

    public V pollValue() {
        return this.poll().getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map.Entry<K, V> peek() {
        if (this.count.get() == 0) {
            return null;
        }
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            Iterator<Map.Entry<K, V>> iterator = this.map.entrySet().iterator();
            if (iterator.hasNext()) {
                Map.Entry<K, V> entry = iterator.next();
                return entry;
            }
            Map.Entry<K, V> entry = null;
            return entry;
        }
        finally {
            takeLock.unlock();
        }
    }

    public V peekValue() {
        return this.peek().getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V get(Object key) {
        if (this.count.get() == 0) {
            return null;
        }
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            V v = this.map.get(key);
            return v;
        }
        finally {
            takeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V getOrDefault(Object key, V defaultValue) {
        if (this.count.get() == 0) {
            return defaultValue;
        }
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            V v = this.map.getOrDefault(key, defaultValue);
            return v;
        }
        finally {
            takeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V remove(Object k) {
        this.fullyLock();
        try {
            V v;
            if (this.map.containsKey(k)) {
                v = this.map.remove(k);
                this.count.decrementAndGet();
            } else {
                v = null;
            }
            if (this.count.get() < this.capacity) {
                this.notFull.signal();
            }
            V v2 = v;
            return v2;
        }
        finally {
            this.fullyUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Object key, Object value) {
        this.fullyLock();
        try {
            boolean remove = this.map.remove(key, value);
            if (remove) {
                this.count.decrementAndGet();
            }
            if (this.count.get() < this.capacity) {
                this.notFull.signal();
            }
            boolean bl = remove;
            return bl;
        }
        finally {
            this.fullyUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeValue(Object v) {
        this.fullyLock();
        try {
            boolean removed = false;
            Iterator<V> iterator = this.map.values().iterator();
            while (iterator.hasNext()) {
                V e = iterator.next();
                if (!Objects.equals(v, e)) continue;
                iterator.remove();
                this.count.decrementAndGet();
                removed = true;
            }
            if (this.count.get() < this.capacity) {
                this.notFull.signal();
            }
            boolean bl = removed;
            return bl;
        }
        finally {
            this.fullyUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean replace(K key, V oldValue, V newValue) {
        this.fullyLock();
        try {
            boolean bl = this.map.replace(key, oldValue, newValue);
            return bl;
        }
        finally {
            this.fullyUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public V replace(K key, V value) {
        this.fullyLock();
        try {
            V v = this.map.replace(key, value);
            return v;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.fullyLock();
        try {
            this.map.replaceAll(function);
        }
        finally {
            this.fullyUnlock();
        }
    }

    public boolean containsKey(Object k) {
        this.fullyLock();
        try {
            boolean bl = this.map.containsKey(k);
            return bl;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public boolean containsValue(Object v) {
        this.fullyLock();
        try {
            boolean bl = this.map.containsValue(v);
            return bl;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public Set<K> keySet() {
        this.fullyLock();
        try {
            Set<K> set = this.map.keySet();
            return set;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public Collection<V> values() {
        this.fullyLock();
        try {
            Collection<V> collection = this.map.values();
            return collection;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public Set<Map.Entry<K, V>> entrySet() {
        this.fullyLock();
        try {
            Set<Map.Entry<K, V>> set = this.map.entrySet();
            return set;
        }
        finally {
            this.fullyUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toArray() {
        this.fullyLock();
        try {
            int size = this.count.get();
            Object[] a = new Object[size];
            int k = 0;
            for (Map.Entry<K, V> value : this.map.entrySet()) {
                a[k++] = value;
            }
            Object[] objectArray = a;
            return objectArray;
        }
        finally {
            this.fullyUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toValueArray() {
        this.fullyLock();
        try {
            int size = this.count.get();
            Object[] a = new Object[size];
            int k = 0;
            for (V value : this.map.values()) {
                a[k++] = value;
            }
            Object[] objectArray = a;
            return objectArray;
        }
        finally {
            this.fullyUnlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T[] toValueArray(T[] a) {
        this.fullyLock();
        try {
            int size = this.count.get();
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
            this.fullyUnlock();
        }
    }

    public int hashCode() {
        this.fullyLock();
        try {
            int n = this.map.hashCode();
            return n;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public String toString() {
        this.fullyLock();
        try {
            String string = this.map.toString();
            return string;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public boolean equals(Object obj) {
        this.fullyLock();
        try {
            boolean bl = this.map.equals(obj);
            return bl;
        }
        finally {
            this.fullyUnlock();
        }
    }

    public void clear() {
        this.fullyLock();
        try {
            this.map.clear();
            if (this.count.getAndSet(0) == this.capacity) {
                this.notFull.signal();
            }
        }
        finally {
            this.fullyUnlock();
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
        boolean signalNotFull = false;
        ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            int n;
            block11: {
                int i;
                int n2 = Math.min(maxElements, this.count.get());
                try {
                    Iterator<V> iterator = this.map.values().iterator();
                    for (i = 0; i < n2 && iterator.hasNext(); ++i) {
                        V next = iterator.next();
                        iterator.remove();
                        c.add(next);
                    }
                    n = n2;
                    if (i <= 0) break block11;
                    signalNotFull = this.count.getAndAdd(-i) == this.capacity;
                }
                catch (Throwable throwable) {
                    if (i > 0) {
                        signalNotFull = this.count.getAndAdd(-i) == this.capacity;
                    }
                    throw throwable;
                }
            }
            return n;
        }
        finally {
            takeLock.unlock();
            if (signalNotFull) {
                this.signalNotFull();
            }
        }
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.fullyLock();
        try {
            this.map.forEach(action);
        }
        finally {
            this.fullyUnlock();
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
        this.fullyLock();
        try {
            s.defaultWriteObject();
            s.writeObject(this.map);
        }
        finally {
            this.fullyUnlock();
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Map map = (Map)s.readObject();
        this.map.putAll(map);
        this.count.set(map.size());
    }

    @Override
    public ConcurrentMap<K, V> map() {
        return new MapImpl(this);
    }

    @Override
    public BlockingQueue<V> queue() {
        return new QueueImpl(this);
    }

    @Deprecated
    private static class Enqueued<V> {
        boolean increased;
        V value;

        Enqueued(boolean increased, V value) {
            this.increased = increased;
            this.value = value;
        }
    }

    private class Itr
    implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Map.Entry<K, V>> iterator;
        final /* synthetic */ AbstractBlockingMapQueueTemp this$0;

        Itr(AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp) {
            AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp2 = abstractBlockingMapQueueTemp;
            Objects.requireNonNull(abstractBlockingMapQueueTemp2);
            this.this$0 = abstractBlockingMapQueueTemp2;
            abstractBlockingMapQueueTemp.fullyLock();
            try {
                this.iterator = abstractBlockingMapQueueTemp.map.entrySet().iterator();
            }
            finally {
                abstractBlockingMapQueueTemp.fullyUnlock();
            }
        }

        @Override
        public boolean hasNext() {
            this.this$0.fullyLock();
            try {
                boolean bl = this.iterator.hasNext();
                return bl;
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public Map.Entry<K, V> next() {
            this.this$0.fullyLock();
            try {
                Map.Entry entry = this.iterator.next();
                return entry;
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public void remove() {
            this.this$0.fullyLock();
            try {
                this.iterator.remove();
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }
    }

    private class ValueItr
    implements Iterator<V> {
        private final Iterator<V> iterator;
        final /* synthetic */ AbstractBlockingMapQueueTemp this$0;

        ValueItr(AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp) {
            AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp2 = abstractBlockingMapQueueTemp;
            Objects.requireNonNull(abstractBlockingMapQueueTemp2);
            this.this$0 = abstractBlockingMapQueueTemp2;
            abstractBlockingMapQueueTemp.fullyLock();
            try {
                this.iterator = abstractBlockingMapQueueTemp.map.values().iterator();
            }
            finally {
                abstractBlockingMapQueueTemp.fullyUnlock();
            }
        }

        @Override
        public boolean hasNext() {
            this.this$0.fullyLock();
            try {
                boolean bl = this.iterator.hasNext();
                return bl;
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public V next() {
            this.this$0.fullyLock();
            try {
                Object v = this.iterator.next();
                return v;
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public void remove() {
            this.this$0.fullyLock();
            try {
                this.iterator.remove();
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }
    }

    private class LBQSpliterator<K, V>
    implements Spliterator<Map.Entry<K, V>> {
        final Spliterator<Map.Entry<K, V>> spliterator;
        final /* synthetic */ AbstractBlockingMapQueueTemp this$0;

        LBQSpliterator(AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp, AbstractBlockingMapQueueTemp<K, V> queue) {
            AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp2 = abstractBlockingMapQueueTemp;
            Objects.requireNonNull(abstractBlockingMapQueueTemp2);
            this.this$0 = abstractBlockingMapQueueTemp2;
            this.spliterator = queue.map.entrySet().spliterator();
        }

        @Override
        public long estimateSize() {
            return this.spliterator.estimateSize();
        }

        @Override
        public Spliterator<Map.Entry<K, V>> trySplit() {
            this.this$0.fullyLock();
            try {
                Spliterator<Map.Entry<K, V>> spliterator = this.spliterator.trySplit();
                return spliterator;
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            this.this$0.fullyLock();
            try {
                this.spliterator.forEachRemaining(action);
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
            this.this$0.fullyLock();
            try {
                boolean bl = this.spliterator.tryAdvance(action);
                return bl;
            }
            finally {
                this.this$0.fullyUnlock();
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
        final /* synthetic */ AbstractBlockingMapQueueTemp this$0;

        ValueLBQSpliterator(AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp, AbstractBlockingMapQueueTemp<K, V> queue) {
            AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp2 = abstractBlockingMapQueueTemp;
            Objects.requireNonNull(abstractBlockingMapQueueTemp2);
            this.this$0 = abstractBlockingMapQueueTemp2;
            this.spliterator = queue.map.values().spliterator();
        }

        @Override
        public long estimateSize() {
            return this.spliterator.estimateSize();
        }

        @Override
        public Spliterator<V> trySplit() {
            this.this$0.fullyLock();
            try {
                Spliterator<V> spliterator = this.spliterator.trySplit();
                return spliterator;
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super V> action) {
            this.this$0.fullyLock();
            try {
                this.spliterator.forEachRemaining(action);
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super V> action) {
            this.this$0.fullyLock();
            try {
                boolean bl = this.spliterator.tryAdvance(action);
                return bl;
            }
            finally {
                this.this$0.fullyUnlock();
            }
        }

        @Override
        public int characteristics() {
            return 4368;
        }
    }

    private class MapImpl
    implements ConcurrentMap<K, V> {
        final /* synthetic */ AbstractBlockingMapQueueTemp this$0;

        private MapImpl(AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp) {
            AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp2 = abstractBlockingMapQueueTemp;
            Objects.requireNonNull(abstractBlockingMapQueueTemp2);
            this.this$0 = abstractBlockingMapQueueTemp2;
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
        final /* synthetic */ AbstractBlockingMapQueueTemp this$0;

        private QueueImpl(AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp) {
            AbstractBlockingMapQueueTemp abstractBlockingMapQueueTemp2 = abstractBlockingMapQueueTemp;
            Objects.requireNonNull(abstractBlockingMapQueueTemp2);
            this.this$0 = abstractBlockingMapQueueTemp2;
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

