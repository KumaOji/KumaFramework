/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.dao.DuplicateKeyException
 */
package com.kuma.boot.idempotent.idempotentenhance.db;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.core.constants.IdempotentConstant;
import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import com.kuma.boot.idempotent.idempotentenhance.db.mapper.BusinessIdempotentMapper;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;

public class MySqlIdempotentRepositoryImpl
implements IdempotentRepository {
    private final BusinessIdempotentMapper businessIdempotentMapper;

    public MySqlIdempotentRepositoryImpl(BusinessIdempotentMapper businessIdempotentMapper) {
        this.businessIdempotentMapper = businessIdempotentMapper;
    }

    @Override
    public boolean create(IdempotentEntity idempotentEntity) {
        try {
            Integer insert = this.businessIdempotentMapper.insert(idempotentEntity);
            return IdempotentConstant.Digit.ONE.equals(insert);
        }
        catch (DuplicateKeyException ex) {
            LogUtils.error((String)"{} DuplicateKeyException.", (Object[])new Object[]{idempotentEntity.getBusinessKey(), ex});
            return false;
        }
    }

    @Override
    public Boolean changeIdempotentStatusProcessing(IdempotentEntity idempotentEntity) {
        Integer update = this.businessIdempotentMapper.changeIdempotentStatusProcessing(idempotentEntity);
        if (IdempotentConstant.Digit.ONE.equals(update)) {
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1L);
            return true;
        }
        return false;
    }

    @Override
    public boolean changeIdempotentStatusSuccess(IdempotentEntity idempotentEntity) {
        Integer update = this.businessIdempotentMapper.changeIdempotentStatusSuccess(idempotentEntity);
        if (IdempotentConstant.Digit.ONE.equals(update)) {
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1L);
            return true;
        }
        return false;
    }

    @Override
    public Boolean delete(IdempotentEntity idempotentEntity) {
        Integer delete = this.businessIdempotentMapper.deleteByUniqueKey(idempotentEntity.getUniqueKey());
        return IdempotentConstant.Digit.ONE.equals(delete);
    }

    @Override
    public Optional<IdempotentEntity> query(IdempotentEntity idempotentEntity) {
        IdempotentEntity entity = this.businessIdempotentMapper.queryByUniqueKey(idempotentEntity.getUniqueKey());
        return Optional.ofNullable(entity);
    }
}

