/*
 *  org.apache.curator.framework.CuratorFramework
 *  org.apache.curator.framework.recipes.locks.InterProcessLock
 *  org.apache.curator.framework.recipes.locks.InterProcessMutex
 *  org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock
 */
package com.kuma.boot.lock.support.zookeeper;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.exception.LockException;
import com.kuma.boot.lock.support.AbstractLockSupport;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

public class CuratorLock
extends AbstractLockSupport<InterProcessLock> {
    private final CuratorFramework curatorFramework;

    public CuratorLock(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    protected InterProcessLock getLock(LockTypeEnums type, String key) {
        if (!key.startsWith("/")) {
            key = "/".concat(key);
        }
        return switch (type) {
            default -> throw new MatchException(null, null);
            case LockTypeEnums.LOCK, LockTypeEnums.FAIR -> new InterProcessMutex(this.curatorFramework, key);
            case LockTypeEnums.READ -> new InterProcessReadWriteLock(this.curatorFramework, key).readLock();
            case LockTypeEnums.WRITE -> new InterProcessReadWriteLock(this.curatorFramework, key).writeLock();
        };
    }

    @Override
    protected boolean tryLock(InterProcessLock lock, long leaseTime, long waitTime, TimeUnit unit) throws Exception {
        return lock.acquire(waitTime, unit);
    }

    @Override
    protected void lock(InterProcessLock lock, long leaseTime, TimeUnit unit) throws Exception {
        lock.acquire();
    }

    @Override
    protected boolean isLocked(InterProcessLock lock) {
        return lock.isAcquiredInThisProcess();
    }

    @Override
    public LockScopeEnum scope() {
        return LockScopeEnum.DISTRIBUTED_LOCK;
    }

    @Override
    protected boolean unlock(String key, InterProcessLock lock) {
        try {
            lock.release();
            return !this.isLocked(lock);
        }
        catch (Exception e) {
            throw new LockException(e);
        }
    }
}

