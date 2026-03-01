/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.repository;

import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;
import java.util.Optional;

public interface IdempotentRepository {
    public boolean create(IdempotentEntity var1);

    public Boolean changeIdempotentStatusProcessing(IdempotentEntity var1);

    public boolean changeIdempotentStatusSuccess(IdempotentEntity var1);

    public Boolean delete(IdempotentEntity var1);

    public Optional<IdempotentEntity> query(IdempotentEntity var1);
}

