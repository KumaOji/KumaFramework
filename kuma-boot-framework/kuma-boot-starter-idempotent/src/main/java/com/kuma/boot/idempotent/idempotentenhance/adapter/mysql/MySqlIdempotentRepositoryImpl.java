package com.kuma.boot.idempotent.idempotentenhance.adapter.mysql;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.idempotentenhance.adapter.mysql.mapper.BusinessIdempotentMapper;
import com.kuma.boot.idempotent.idempotentenhance.core.constants.IdempotentConstant;
import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;
import com.kuma.boot.idempotent.idempotentenhance.core.repository.IdempotentRepository;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

/**
 * 基于MySQL的repository实现
 *
 * @author wenpan 2023/01/02 21:59
 */
public class MySqlIdempotentRepositoryImpl implements IdempotentRepository {

    private final BusinessIdempotentMapper businessIdempotentMapper;

    public MySqlIdempotentRepositoryImpl( BusinessIdempotentMapper businessIdempotentMapper) {
        this.businessIdempotentMapper = businessIdempotentMapper;
    }

    @Override
    public boolean create(IdempotentEntity idempotentEntity) {
        try {
            Integer insert = businessIdempotentMapper.insert(idempotentEntity);
            return IdempotentConstant.Digit.ONE.equals(insert);
        } catch (DuplicateKeyException ex) {
            LogUtils.error("{} DuplicateKeyException.", idempotentEntity.getBusinessKey(), ex);
            return false;
        }
    }

    @Override
    public Boolean changeIdempotentStatusProcessing(IdempotentEntity idempotentEntity) {
        Integer update = businessIdempotentMapper.changeIdempotentStatusProcessing(idempotentEntity);
        // 更新成功后将版本号同步 +1
        if(IdempotentConstant.Digit.ONE.equals(update)){
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean changeIdempotentStatusSuccess(IdempotentEntity idempotentEntity) {
        Integer update = businessIdempotentMapper.changeIdempotentStatusSuccess(idempotentEntity);
        // 更新成功后将版本号同步 +1
        if(IdempotentConstant.Digit.ONE.equals(update)){
            idempotentEntity.setObjectVersionNumber(idempotentEntity.getObjectVersionNumber() + 1);
            return true;
        }
        return false;
    }

    @Override
    public Boolean delete(IdempotentEntity idempotentEntity) {
        Integer delete = businessIdempotentMapper.deleteByUniqueKey(idempotentEntity.getUniqueKey());
        return IdempotentConstant.Digit.ONE.equals(delete);
    }

    @Override
    public Optional<IdempotentEntity> query(IdempotentEntity idempotentEntity) {
        // 通过唯一键查询
        IdempotentEntity entity = businessIdempotentMapper.queryByUniqueKey(idempotentEntity.getUniqueKey());
        return Optional.ofNullable(entity);
    }
}
