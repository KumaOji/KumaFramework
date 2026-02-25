/*
 *  org.redisson.api.RLock
 *  org.redisson.api.RedissonClient
 */
package com.kuma.boot.lock.support.redisson;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.support.AbstractLockSupport;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public class RedissonLock
extends AbstractLockSupport<RLock> {
    private final RedissonClient redissonClient;

    public RedissonLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    protected RLock getLock(LockTypeEnums type, String key) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case LockTypeEnums.LOCK -> this.redissonClient.getLock(key);
            case LockTypeEnums.FAIR -> this.redissonClient.getFairLock(key);
            case LockTypeEnums.READ -> this.redissonClient.getReadWriteLock(key).readLock();
            case LockTypeEnums.WRITE -> this.redissonClient.getReadWriteLock(key).writeLock();
        };
    }

    @Override
    protected boolean tryLock(RLock lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        return lock.tryLock(waitTime, leaseTime, unit);
    }

    @Override
    protected void lock(RLock lock, long leaseTime, TimeUnit unit) {
        lock.lock(leaseTime, unit);
    }

    @Override
    protected boolean tryLockAsync(RLock lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        return (Boolean)lock.tryLockAsync(waitTime, leaseTime, unit).get();
    }

    @Override
    protected void lockAsync(RLock lock, long leaseTime, TimeUnit unit) throws Exception {
        lock.lockAsync(leaseTime, unit).get();
    }

    @Override
    protected boolean isLocked(RLock lock) {
        return lock.isLocked() && lock.isHeldByCurrentThread();
    }

    @Override
    public LockScopeEnum scope() {
        return LockScopeEnum.DISTRIBUTED_LOCK;
    }

    @Override
    protected boolean supportAsync() {
        return true;
    }

    @Override
    protected boolean unlock(String key, RLock lock) {
        lock.unlock();
        return !this.isLocked(lock);
    }
}

