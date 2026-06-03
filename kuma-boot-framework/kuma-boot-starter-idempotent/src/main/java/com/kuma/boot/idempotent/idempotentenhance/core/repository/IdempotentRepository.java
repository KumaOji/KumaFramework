package com.kuma.boot.idempotent.idempotentenhance.core.repository;

import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;

import java.util.Optional;

/**
 * 幂等 repository
 *
 * @author wenpan 2022/12/31 15:19
 */
public interface IdempotentRepository {

    /**
     * 保存幂等实体
     *
     * @param idempotentEntity 幂等实体
     * @return java.lang.Integer 响应行数
     * @author wenpan 2022/12/31 3:27 下午
     */
    boolean create(IdempotentEntity idempotentEntity);

    /**
     * 修改幂等状态
     *
     * @param idempotentEntity 待修改的实体
     * @return java.lang.Boolean 是否修改成功
     * @author wenpan 2023/1/1 10:28 下午
     */
    Boolean changeIdempotentStatusProcessing(IdempotentEntity idempotentEntity);

    /**
     * 幂等状态变更为成功
     *
     * @param idempotentEntity 待变更的对象
     * @return java.lang.Boolean
     * @author wenpan 2023/1/1 11:15 下午
     */
    boolean changeIdempotentStatusSuccess(IdempotentEntity idempotentEntity);

    /**
     * 删除记录
     *
     * @param idempotentEntity 幂等记录
     * @return java.lang.Boolean
     * @author wenpan 2023/1/2 2:21 下午
     */
    Boolean delete(IdempotentEntity idempotentEntity);

    /**
     * 查询幂等记录
     *
     * @param idempotentEntity 查询条件
     * @return java.util.Optional<org.enhance.idempotent.core.pojo.IdempotentEntity>
     * @author wenpan 2023/1/2 9:49 下午
     */
    Optional<IdempotentEntity> query(IdempotentEntity idempotentEntity);

}
