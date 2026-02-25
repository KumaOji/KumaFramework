/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.lock.support.local;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.support.AbstractLockSupport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalLock
extends AbstractLockSupport<Lock> {
    private static final Map<String, Lock> CACHE_LOCK = new ConcurrentHashMap<String, Lock>();

    @Override
    protected Lock getLock(LockTypeEnums type, String key) {
        return CACHE_LOCK.computeIfAbsent(key, s -> switch (type) {
            default -> throw new MatchException(null, null);
            case LockTypeEnums.LOCK -> new ReentrantLock();
            case LockTypeEnums.FAIR -> new ReentrantLock(true);
            case LockTypeEnums.READ -> new ReentrantReadWriteLock().readLock();
            case LockTypeEnums.WRITE -> new ReentrantReadWriteLock().writeLock();
        });
    }

    @Override
    protected boolean tryLock(Lock lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        return lock.tryLock(waitTime, unit);
    }

    @Override
    protected void lock(Lock lock, long leaseTime, TimeUnit unit) {
        lock.lock();
    }

    @Override
    protected boolean unlock(String key, Lock lock) {
        lock.unlock();
        return !this.isLocked(lock);
    }

    @Override
    protected boolean isLocked(Lock lock) {
        if (lock instanceof ReentrantLock) {
            ReentrantLock reentrantLock = (ReentrantLock)lock;
            return reentrantLock.isLocked() && reentrantLock.isHeldByCurrentThread();
        }
        if (lock instanceof ReentrantReadWriteLock.WriteLock) {
            ReentrantReadWriteLock.WriteLock writeLock = (ReentrantReadWriteLock.WriteLock)lock;
            return writeLock.getHoldCount() != 0 && writeLock.isHeldByCurrentThread();
        }
        return false;
    }

    @Override
    public LockScopeEnum scope() {
        return LockScopeEnum.STANDALONE_LOCK;
    }
}

