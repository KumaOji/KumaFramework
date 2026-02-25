/*
 *  org.springframework.core.Ordered
 */
package com.kuma.boot.lock.support;

import com.kuma.boot.lock.enums.LockScopeEnum;
import com.kuma.boot.lock.enums.LockTypeEnums;
import java.util.concurrent.TimeUnit;
import org.springframework.core.Ordered;

public interface DistributedLock
extends Ordered {
    public boolean tryLock(LockTypeEnums var1, String var2, long var3, long var5, TimeUnit var7, boolean var8);

    default public boolean tryLock(LockTypeEnums type, String key, long waitTime, long leaseTime, TimeUnit unit) {
        return this.tryLock(type, key, waitTime, leaseTime, unit, false);
    }

    default public boolean tryLock(LockTypeEnums type, String key, long waitTime, TimeUnit unit) {
        return this.tryLock(type, key, -1L, waitTime, unit, false);
    }

    default public boolean tryLock(String key, long waitTime, TimeUnit unit) {
        return this.tryLock(LockTypeEnums.LOCK, key, -1L, waitTime, unit, false);
    }

    public void lock(LockTypeEnums var1, String var2, long var3, TimeUnit var5, boolean var6);

    default public void lock(LockTypeEnums type, String key, long leaseTime, TimeUnit unit) {
        this.lock(type, key, leaseTime, unit, false);
    }

    default public void lock(LockTypeEnums type, String key) {
        this.lock(type, key, -1L, null);
    }

    default public void lock(String key) {
        this.lock(LockTypeEnums.LOCK, key, -1L, null, false);
    }

    public void unlock();

    public LockScopeEnum scope();

    default public int getOrder() {
        return 0;
    }
}

