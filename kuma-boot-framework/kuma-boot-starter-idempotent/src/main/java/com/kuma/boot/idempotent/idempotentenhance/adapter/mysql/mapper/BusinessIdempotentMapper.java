package com.kuma.boot.idempotent.idempotentenhance.adapter.mysql.mapper;

import com.kuma.boot.idempotent.idempotentenhance.core.pojo.IdempotentEntity;

/**
 * 幂等mapper
 *
 * @author wenpanfeng 2023/01/04 18:03
 */
public interface BusinessIdempotentMapper {

    /**
     * 插入db
     *
     * @param idempotentEntity 待插入的实体
     * @return java.lang.Integer 影响行数
     * @author wenpanfeng 2023/1/4 18:09
     */
    Integer insert(IdempotentEntity idempotentEntity);

    /**
     * 修改幂等状态为处理中
     *
     * @param idempotentEntity 待修改的实体
     * @return java.lang.Integer 影响行数
     * @author wenpanfeng 2023/1/4 18:10
     */
    Integer changeIdempotentStatusProcessing(IdempotentEntity idempotentEntity);

    /**
     * 修改幂等状态为成功
     *
     * @param idempotentEntity 待修改的实体
     * @return java.lang.Integer 影响行数
     * @author wenpanfeng 2023/1/4 18:10
     */
    Integer changeIdempotentStatusSuccess(IdempotentEntity idempotentEntity);

    /**
     * 按照唯一键删除
     *
     * @param uniqueKey 唯一键
     * @return java.lang.Integer 影响行数
     * @author wenpanfeng 2023/1/4 18:13
     */
    Integer deleteByUniqueKey(String uniqueKey);

    /**
     * 查询幂等记录
     *
     * @param uniqueKey 唯一键
     * @return org.enhance.idempotent.core.pojo.IdempotentEntity
     * @author wenpanfeng 2023/1/4 18:14
     */
    IdempotentEntity queryByUniqueKey(String uniqueKey);

}
