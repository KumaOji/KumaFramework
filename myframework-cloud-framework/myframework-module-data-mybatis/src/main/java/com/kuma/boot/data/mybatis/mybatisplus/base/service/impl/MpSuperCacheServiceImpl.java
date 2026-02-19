/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.mybatisplus.base.service.impl;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kuma.boot.cache.redis.model.CacheKey;
import com.kuma.boot.cache.redis.model.CacheKeyBuilder;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.data.mybatis.mybatisplus.base.entity.MpSuperEntity;
import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapperOther;
import com.kuma.boot.data.mybatis.mybatisplus.base.service.MpSuperCacheService;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.SqlSession;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于 CacheOps 实现的 缓存实现 默认的key规则： #{CacheKeyBuilder#key()}:id
 *
 * <p>1，getByIdCache：新增的方法： 先查缓存，在查db
 * <p>2，removeById：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * <p>3，removeByIds：重写ServiceImpl 类的方法，删除db后，淘汰缓存
 * <p>4，updateAllById： 新增的方法： 修改数据（所有字段）后，淘汰缓存
 * <p>5，updateById：重写 ServiceImpl类的方法，修改db后，淘汰缓存
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:21:08
 */
public abstract class MpSuperCacheServiceImpl<
        M extends MpSuperMapperOther<T, I>, T extends MpSuperEntity<I>, I extends Serializable>
        extends com.kuma.boot.data.mybatis.mybatisplus.base.service.impl.MpSuperServiceImpl<M, T, I> implements MpSuperCacheService<T, I> {

    @Autowired protected RedisRepository redisRepository;

    protected static final int MAX_BATCH_KEY_SIZE = 20;

    protected CacheKeyBuilder cacheKeyBuilder() {
        return () -> super.getEntityClass().getSimpleName();
    }

    @Override
    @Transactional(readOnly = true)
    public T getByIdCache(Serializable id) {
        CacheKey cacheKey = cacheKeyBuilder().key(id);
        return redisRepository.get(cacheKey, k -> super.getById(id));
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<T> findByIds(
            @NonNull Collection<I> ids, Function<Collection<I>, Collection<T>> loader) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        // 拼接keys
        List<CacheKey> keys = ids.stream().map(cacheKeyBuilder()::key).toList();
        // 切割
        List<List<CacheKey>> partitionKeys = Lists.partition(keys, MAX_BATCH_KEY_SIZE);

        // 用切割后的 partitionKeys 分批去缓存查， 返回的是缓存中存在的数据
        List<T> valueList =
                partitionKeys.stream()
                        .map(ks -> (List<T>) redisRepository.findByListCacheKey(ks))
                        .flatMap(Collection::stream)
                        .toList();

        // 所有的key
        List<I> keysList = Lists.newArrayList(ids);
        // 缓存不存在的key
        Set<I> missedKeys = Sets.newLinkedHashSet();

        List<T> allList = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            I k = keysList.get(i);
            T v = valueList.get(i);

            if (v == null) {
                missedKeys.add(k);
            } else {
                allList.add(v);
            }
        }
        // 加载miss 的数据，并设置到缓存
        if (CollUtil.isNotEmpty(missedKeys)) {
            if (loader == null) {
                loader = this::listByIds;
            }
            Collection<T> missList = loader.apply(missedKeys);
            missList.forEach(this::setCache);
            allList.addAll(missList);
        }
        return allList;
    }

    @Override
    @Transactional(readOnly = true)
    public T getByKey(CacheKey key, Function<CacheKey, Object> loader) {
        Object id = redisRepository.get(key, loader);
        return id == null ? null : getByIdCache(Convert.toLong(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        delCache(id);
        return bool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        boolean flag = super.removeByIds(idList);

        delCache(idList);
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(T model) {
        boolean save = super.save(model);
        setCache(model);
        return save;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllById(T model) {
        boolean updateBool = super.updateAllById(model);
        delCache(model);
        return updateBool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        delCache(model);
        return updateBool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(
                entityList,
                batchSize,
                (sqlSession, entity) -> {
                    sqlSession.insert(sqlStatement, entity);

                    // 设置缓存
                    setCache(entity);
                });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        Assert.notNull(
                tableInfo,
                "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(
                keyProperty,
                "error: can not execute. because can not find column for id from entity!");

        BiPredicate<SqlSession, T> predicate =
                (sqlSession, entity) -> {
                    Object idVal = SystemMetaObject.forObject(entity).getValue(keyProperty);
                    return StringUtils.checkValNull(idVal)
                            || CollectionUtils.isEmpty(
                            sqlSession.selectList(
                                    getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
                };

        BiConsumer<SqlSession, T> consumer =
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);

                    // 清理缓存
                    delCache(entity);
                };

        String sqlStatement =
                SqlHelper.getSqlStatement(this.getMapperClass(), SqlMethod.INSERT_ONE);
        return SqlHelper.executeBatch(
                this.getSqlSessionFactory(),
                log,
                entityList,
                batchSize,
                (sqlSession, entity) -> {
                    if (predicate.test(sqlSession, entity)) {
                        sqlSession.insert(sqlStatement, entity);
                        // 设置缓存
                        setCache(entity);
                    } else {
                        consumer.accept(sqlSession, entity);
                    }
                });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(
                entityList,
                batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement, param);

                    // 清理缓存
                    delCache(entity);
                });
    }

    @Override
    public void refreshCache() {
        list().forEach(this::setCache);
    }

    @Override
    public void clearCache() {
        list().forEach(this::delCache);
    }

    protected void delCache(Serializable... ids) {
        delCache(Arrays.asList(ids));
    }

    protected void delCache(Collection<?> idList) {
        CacheKey[] keys =
                idList.stream().map(id -> cacheKeyBuilder().key(id)).toArray(CacheKey[]::new);
        redisRepository.del(keys);
    }

    protected void delCache(T model) {
        Object id = getId(model);
        if (id != null) {
            CacheKey key = cacheKeyBuilder().key(id);
            redisRepository.del(key);
        }
    }

    protected void setCache(T model) {
        Object id = getId(model);
        if (id != null) {
            CacheKey key = cacheKeyBuilder().key(id);
            redisRepository.set(key, model);
        }
    }

    protected Object getId(T model) {
        if (model instanceof MpSuperEntity) {
            return ((MpSuperEntity) model).getId();
        } else {
            // 实体没有继承 Entity 和 SuperEntity
            TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
            if (tableInfo == null) {
                return null;
            }
            // 主键类型
            Class<?> keyType = tableInfo.getKeyType();
            if (keyType == null) {
                return null;
            }
            // id 字段名
            String keyProperty = tableInfo.getKeyProperty();

            // 反射得到 主键的值
            Field idField = ReflectUtil.getField(getEntityClass(), keyProperty);
            return ReflectUtil.getFieldValue(model, idField);
        }
    }
}
