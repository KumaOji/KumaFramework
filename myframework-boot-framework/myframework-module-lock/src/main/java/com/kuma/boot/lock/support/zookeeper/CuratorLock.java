/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.lock.support.zookeeper;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import com.kuma.boot.lock.exception.LockException;
import com.kuma.boot.lock.support.AbstractLockSupport;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * CuratorLock
 * </p>
 */
public class CuratorLock extends AbstractLockSupport<InterProcessLock> {

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
            case LOCK, FAIR -> new InterProcessMutex(curatorFramework, key);
            case READ -> new InterProcessReadWriteLock(curatorFramework, key).readLock();
            case WRITE -> new InterProcessReadWriteLock(curatorFramework, key).writeLock();
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
            return !isLocked(lock);
        } catch (Exception e) {
            throw new LockException(e);
        }
    }
}
