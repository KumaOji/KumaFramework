/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.db.mapper;

import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;

public interface BusinessIdempotentMapper {
    public Integer insert(IdempotentEntity var1);

    public Integer changeIdempotentStatusProcessing(IdempotentEntity var1);

    public Integer changeIdempotentStatusSuccess(IdempotentEntity var1);

    public Integer deleteByUniqueKey(String var1);

    public IdempotentEntity queryByUniqueKey(String var1);
}

