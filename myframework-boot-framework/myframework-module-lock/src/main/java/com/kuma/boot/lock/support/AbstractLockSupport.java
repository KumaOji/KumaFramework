/*
 *  com.kuma.boot.common.support.tuple.impl.Pair
 */
package com.kuma.boot.lock.support;

import com.kuma.boot.common.support.tuple.impl.Pair;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.exception.LockException;
import com.kuma.boot.lock.exception.UnSupportLockException;

import java.util.concurrent.TimeUnit;

public abstract class AbstractLockSupport<T>
implements DistributedLock {
    protected final ThreadLocal<Pair<String, T>> threadLocal = new InheritableThreadLocal<Pair<String, T>>();

    @Override
    public boolean tryLock(LockTypeEnums type, String key, long leaseTime, long waitTime, TimeUnit unit, boolean async) {
        T lock = this.getLock(type, key);
        try {
            boolean isLocked;
            boolean bl = isLocked = this.supportAsync() && async ? this.tryLockAsync(lock, leaseTime, waitTime, unit) : this.tryLock(lock, leaseTime, waitTime, unit);
            if (isLocked) {
                this.threadLocal.set(Pair.of((Object)key, lock));
            }
            return isLocked;
        }
        catch (Exception e) {
            this.threadLocal.remove();
            throw new LockException(e);
        }
    }

    @Override
    public void lock(LockTypeEnums type, String key, long leaseTime, TimeUnit unit, boolean async) {
        T lock = this.getLock(type, key);
        try {
            if (this.supportAsync() && async) {
                this.lockAsync(lock, leaseTime, unit);
            } else {
                this.lock(lock, leaseTime, unit);
            }
            this.threadLocal.set(Pair.of((Object)key, lock));
        }
        catch (Exception e) {
            this.threadLocal.remove();
            throw new LockException(e);
        }
    }

    @Override
    public void unlock() {
        Pair<String, T> pair = this.threadLocal.get();
        if (pair != null) {
            String key = (String)pair.getValueOne();
            Object lock = pair.getValueTwo();
            if (this.isLocked(lock) && this.unlock(key, lock)) {
                this.threadLocal.remove();
            }
        }
    }

    protected abstract T getLock(LockTypeEnums var1, String var2);

    protected abstract boolean unlock(String var1, T var2);

    protected boolean tryLockAsync(T lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        throw new UnSupportLockException("Async lock of " + this.getClass().getSimpleName() + " isn't support");
    }

    protected abstract boolean tryLock(T var1, long var2, long var4, TimeUnit var6) throws Exception;

    protected void lockAsync(T lock, long leaseTime, TimeUnit unit) throws Exception {
        throw new UnSupportLockException("Async lock of " + this.getClass().getSimpleName() + " isn't support");
    }

    protected abstract void lock(T var1, long var2, TimeUnit var4) throws Exception;

    protected abstract boolean isLocked(T var1);

    protected boolean supportAsync() {
        return false;
    }
}

