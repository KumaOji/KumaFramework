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

package com.kuma.boot.data.mybatis.mybatisplus.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.data.mybatis.mybatisplus.base.entity.MpSuperEntity;
import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapperOther;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * 基于MP的 IService 新增了2个方法： saveBatchSomeColumn、updateAllById 其中： 1，updateAllById 执行后，会清除缓存
 * 2，saveBatchSomeColumn 批量插入
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:21:51
 */
public interface MpSuperService<T extends MpSuperEntity<I>, I extends Serializable>
        extends IService<T> {

    JdbcClient jdbcClient();

    JdbcTemplate jdbcTemplate();

    /**
     * 批量保存数据
     *
     * <p>注意：该方法仅仅测试过mysql
     *
     * @return boolean
     * @since 2021-09-02 21:22:06
     */
    default boolean saveBatchSomeColumn(List<T> entityList) {
        if (entityList.isEmpty()) {
            return true;
        }
        if (entityList.size() > 5000) {
            throw new BusinessException("太多数据啦");
        }
        MpSuperMapperOther baseMapper = (MpSuperMapperOther) getBaseMapper();
        @SuppressWarnings("unchecked")
        int i = baseMapper.insertBatchSomeColumn(entityList);
        return SqlHelper.retBool(i);
    }

    /**
     * 根据id修改 entity 的所有字段
     *
     * @param entity entity
     * @return boolean
     * @since 2021-09-02 21:22:14
     */
    boolean updateAllById(T entity);

    boolean deleteById(I id);

    /**
     * 构建mybatis-plus的query
     * @return
     */
    QueryChainWrapper<T> query();

    /**
     * 构建mybatis-plus的lambdaQuery
     * @return
     */
    LambdaQueryChainWrapper<T> lambdaQuery();

    /**
     * 构建mybatis-plus的update
     * @return
     */
    UpdateChainWrapper<T> update();

    /**
     * 构建mybatis-plus的lambdaUpdate
     * @return
     */
    LambdaUpdateChainWrapper<T> lambdaUpdate();

    /**
     * 获取Entity实体
     * @param id 主键
     * @return entity
     */
    T getEntity(Serializable id);

    /**
     * 更新Entity实体（更新符合条件的所有非空字段）
     * @param entity
     * @param updateCriteria
     * @return
     */
    boolean updateEntity(T entity, Wrapper<T> updateCriteria);

    boolean updateEntity(T entity);

    /**
     * 更新Entity实体（仅更新updateWrapper.set指定的字段）
     * @param updateWrapper
     * @return
     */
    boolean updateEntity(Wrapper<T> updateWrapper);

    /**
     * 按条件删除实体
     * @param queryWrapper
     * @return
     * @throws Exception
     */
    boolean deleteEntities(Wrapper<T> queryWrapper);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean deleteEntity(Serializable id);

    /**
     * 根据主键删除实体
     * @param fieldKey 字段名
     * @param fieldVal 字段值
     * @return true:成功, false:失败
     */
    boolean deleteEntity(String fieldKey, Object fieldVal);

    /**
     * 批量删除指定id的实体
     * @param entityIds
     * @return
     * @throws Exception
     */
    boolean deleteEntities(Collection<? extends Serializable> entityIds);

    /**
     * 获取符合条件的entity记录总数
     * @return
     */
    long getEntityListCount(Wrapper<T> queryWrapper);

    /**
     * 获取指定条件的Entity集合
     * @param queryWrapper
     * @return
     * @throws Exception
     */
    List<T> getEntityList(Wrapper<T> queryWrapper);

    /**
     * 获取指定条件的Entity集合
     * @param queryWrapper
     * @param pagination
     * @return
     * @throws Exception
     */
    // List<T> getEntityList(Wrapper queryWrapper, Pagination pagination);

    /**
     * 获取指定条件的Entity集合
     * @param ids
     * @return
     */
    List<T> getEntityListByIds(List<?> ids);

    /**
     * 获取entity某个属性值
     * @param idVal id值
     * @param getterFn 返回属性getter
     * @return
     */
    // <FT> FT getValueOfField(Serializable idVal, SFunction<T, FT> getterFn);

    /**
     * 获取entity某个属性值
     * @param idFieldFn 查询字段
     * @param idVal 查询字段值
     * @param getterFn 返回属性getter
     * @return
     */
    <FT> FT getValueOfField(
            SFunction<T, ?> idFieldFn, Serializable idVal, SFunction<T, FT> getterFn);

    /**
     * 获取entity某个属性值
     *
     * @param queryWrapper
     * @param getterFn
     * @return
     * @param <FT>
     */
    <FT> FT getValueOfField(LambdaQueryWrapper<T> queryWrapper, SFunction<T, FT> getterFn);

    /**
     * 根据查询条件获取vo树形结构
     * @param rootNodeId
     * @param voClass
     * @param getParentIdsPath
     * @param getSortId
     * @return
     * @throws Exception
     */
    <VO> List<VO> getViewObjectTree(
            Serializable rootNodeId,
            Class<VO> voClass,
            SFunction<T, String> getParentIdsPath,
            SFunction<T, Comparable<?>> getSortId);

    List<T> getEntityListLimit(Wrapper<T> queryWrapper, int limitCount);

    <VO> List<VO> getViewObjectList(Wrapper<T> queryWrapper, Class<VO> voClass);
}
