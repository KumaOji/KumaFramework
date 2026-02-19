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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.tree.TreeUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.data.mybatis.mybatisplus.base.entity.MpSuperEntity;
import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapperOther;
import com.kuma.boot.data.mybatis.mybatisplus.base.service.MpSuperService;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

/**
 * 不含缓存的Service实现
 *
 * <p>2，removeById：重写 ServiceImpl 类的方法，删除db
 * <p>3，removeByIds：重写 ServiceImpl 类的方法，删除db
 * <p>4，updateAllById：新增的方法： 修改数据（所有字段）
 * <p>5，updateById：重写 ServiceImpl 类的方法，修改db后
 *
 * @param <M> Mapper
 * @param <T> 实体
 * @param <I> 实体主键类型
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:22:34
 */
public class MpSuperServiceImpl<
        M extends MpSuperMapperOther<T, I>, T extends MpSuperEntity<I>, I extends Serializable>
        extends ServiceImpl<M, T> implements MpSuperService<T, I> {

    private Class<T> entityClass = null;

    public MpSuperMapperOther<T, I> getSuperMapper() {
        if (baseMapper != null) {
            return baseMapper;
        }
        throw new BusinessException("未查询到mapper");
    }

    @Resource private JdbcClient jdbcClient;

    @Override
    public JdbcClient jdbcClient() {
        return jdbcClient;
    }

    @Resource private JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate jdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            @SuppressWarnings("unchecked")
            Class<T> clazz =
                    (Class<T>)
                            ((ParameterizedType) this.getClass().getGenericSuperclass())
                                    .getActualTypeArguments()[1];
            this.entityClass = clazz;
        }
        return this.entityClass;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(T model) {
        return super.save(model);
    }

    /**
     * 处理新增相关处理
     *
     * @param model 实体
     * @return {@link Result }
     * @since 2021-09-02 21:22:52
     */
    protected Result<T> handlerSave(T model) {
        return Result.success(model);
    }

    /**
     * 处理修改相关处理
     *
     * @param model 实体
     * @return {@link Result }
     * @since 2021-09-02 21:23:00
     */
    protected Result<T> handlerUpdateAllById(T model) {
        return Result.success(model);
    }

    /**
     * 处理修改相关处理
     *
     * @param model 实体
     * @return {@link Result }
     * @since 2021-09-02 21:23:06
     */
    protected Result<T> handlerUpdateById(T model) {
        return Result.success(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllById(T model) {

        return SqlHelper.retBool(getSuperMapper().updateAllById(model));
    }

    @Override
    public boolean deleteById(I id) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(T model) {
        return super.updateById(model);
    }

    @Override
    public QueryChainWrapper<T> query() {
        return ChainWrappers.queryChain(this.getBaseMapper());
    }

    @Override
    public LambdaQueryChainWrapper<T> lambdaQuery() {
        return ChainWrappers.lambdaQueryChain(this.getBaseMapper());
    }

    @Override
    public UpdateChainWrapper<T> update() {
        return ChainWrappers.updateChain(this.getBaseMapper());
    }

    @Override
    public LambdaUpdateChainWrapper<T> lambdaUpdate() {
        return ChainWrappers.lambdaUpdateChain(this.getBaseMapper());
    }

    @Override
    public T getEntity(Serializable id) {
        return super.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateEntity(T entity) {
        this.beforeUpdate(entity);
        boolean success = super.updateById(entity);
        if (success) {
            this.afterUpdate(entity);
        }
        return success;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateEntity(T entity, Wrapper<T> updateWrapper) {
        this.beforeUpdate(entity);
        boolean success = super.update(entity, updateWrapper);
        if (success) {
            this.afterUpdate(entity);
        }
        return success;
    }

    @Override
    public boolean updateEntity(Wrapper<T> updateWrapper) {
        return super.update(null, updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteEntity(Serializable id) {
        Class<T> entityClass = getEntityClass();
        // 树结构，仅允许叶子节点进行删除操作
        //		if(BaseTreeEntity.class.isAssignableFrom(entityClass)) {
        //			QueryWrapper<T> wrapper = new QueryWrapper<T>().eq(Cons.ColumnName.parent_id.name(),
        // id);
        //			if(exists(wrapper)) {
        //				throw new BusinessException(Status.FAIL_VALIDATION, "当前节点下存在下级节点，不允许被删除！");
        //			}
        //		}
        //		String pk = ContextHolder.getIdFieldName(entityClass);
        // this.beforeDelete("id", id);
        boolean success = super.removeById(id);
        if (success) {
            // this.afterDelete("id", id);
        }
        return success;
    }

    @Override
    public boolean deleteEntity(String fieldKey, Object fieldVal) {
        // 获取主键的关联属性
        // PropInfo propInfo = BindingCacheManager.getPropInfoByClass(entityClass);
        // String column = propInfo.getColumnByField(fieldKey);
        // if(column == null) {
        //	column = fieldKey;
        // }
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().eq(fieldKey, fieldVal);
        // this.beforeDelete(fieldKey, fieldVal);
        boolean success = super.remove(queryWrapper);
        if (success) {
            // this.afterDelete(fieldKey, fieldVal);
        }
        return success;
    }

    @Override
    public boolean deleteEntities(Wrapper<T> queryWrapper) {
        return super.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEntities(Collection<? extends Serializable> entityIds) {
        if (CollUtil.isEmpty(entityIds)) {
            return false;
        }
        // String pk = ContextHolder.getIdFieldName(entityClass);
        // this.beforeDelete(pk, entityIds);
        boolean success = super.removeByIds(entityIds);
        if (success) {
            // this.afterDelete(pk, entityIds);
        }
        return success;
    }

    @Override
    public long getEntityListCount(Wrapper<T> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    public List<T> getEntityList(Wrapper<T> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public List<T> getEntityListByIds(List<?> ids) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        queryWrapper.in("id", ids);
        return getEntityList(queryWrapper);
    }

    //	@Override
    //	public <FT> FT getValueOfField(Serializable idVal, SFunction<T, FT> getterFn) {
    //		PropInfo propInfo = BindingCacheManager.getPropInfoByClass(entityClass);
    //		String fetchCol =
    // propInfo.getColumnByField(BeanUtils.convertSFunctionToFieldName(getterFn));
    //		QueryWrapper<T> queryWrapper = new QueryWrapper<T>()
    //			.select(propInfo.getIdColumn(), fetchCol)
    //			.eq(propInfo.getIdColumn(), idVal);
    //		T entity = getSingleEntity(queryWrapper);
    //		if(entity == null){
    //			return null;
    //		}
    //		return getterFn.apply(entity);
    //	}

    @Override
    public <FT> FT getValueOfField(
            SFunction<T, ?> queryFieldFn, Serializable queryFieldVal, SFunction<T, FT> getterFn) {
        return getValueOfField(Wrappers.<T>lambdaQuery().eq(queryFieldFn, queryFieldVal), getterFn);
    }

    @Override
    public <FT> FT getValueOfField(LambdaQueryWrapper<T> queryWrapper, SFunction<T, FT> getterFn) {
        T entity = getSingleEntity(queryWrapper.select(getterFn));
        if (entity == null) {
            return null;
        }
        return getterFn.apply(entity);
    }

    @Override
    public <VO> List<VO> getViewObjectTree(
            Serializable rootNodeId,
            Class<VO> voClass,
            SFunction<T, String> getParentIdsPath,
            SFunction<T, Comparable<?>> getSortId) {
        LambdaQueryWrapper<T> queryWrapper = Wrappers.lambdaQuery();
        // WrapperHelper.optimizeSelect(queryWrapper, getEntityClass(), voClass);
        // 排序
        queryWrapper.orderByAsc(getSortId != null, getSortId);

        if (ObjectUtils.isEmpty(rootNodeId)) {
            return TreeUtils.buildTree(getViewObjectList(queryWrapper, voClass));
        }
        T entity = getEntity(rootNodeId);
        // 无根节点
        if (entity == null) {
            return Collections.emptyList();
        }
        if (getParentIdsPath != null) {
            String parentPath = getParentIdsPath.apply(entity);
            if (StrUtil.isEmpty(parentPath)) {
                // parentPath格式：/([0-9a-f]+,)+/g
                parentPath = rootNodeId + ",";
            }
            queryWrapper.likeRight(getParentIdsPath, parentPath);
        }
        List<T> entityList = getEntityList(queryWrapper);
        if (ObjectUtils.isEmpty(rootNodeId)) {
            entityList.add(entity);
        }

        // 自动转换为VO并绑定关联对象
        List<VO> voList = BeanUtil.copyToList(entityList, voClass);
        return TreeUtils.buildTree(voList, rootNodeId);
    }

    @Override
    public <VO> List<VO> getViewObjectList(Wrapper<T> queryWrapper, Class<VO> voClass) {
        // WrapperHelper.optimizeSelect(queryWrapper, getEntityClass(), voClass);
        List<T> entityList = getEntityList(queryWrapper);
        // 自动转换为VO并绑定关联对象
        return BeanUtil.copyToList(entityList, voClass);
    }

    /**
     * 用于更新之前的自动填充等场景调用
     */
    protected void beforeUpdate(T entity) {
        //		if(entity instanceof BaseTreeEntity) {
        //			fillTreeNodeParentPath(entity);
        //		}
    }

    /**
     * 创建数据的后拦截
     * @param entity
     */
    protected void afterCreate(T entity) {}

    /**
     * 更新数据的后拦截
     * @param entity
     */
    protected void afterUpdate(T entity) {}

    public T getSingleEntity(Wrapper<T> queryWrapper) {
        // 如果是动态join，则调用JoinsBinder
        //		if(queryWrapper instanceof DynamicJoinQueryWrapper){
        //			return (T)Binder.joinQueryOne((DynamicJoinQueryWrapper)queryWrapper, entityClass);
        //		}
        List<T> entityList = getEntityListLimit(queryWrapper, 1);
        if (CollUtil.isNotEmpty(entityList)) {
            return entityList.get(0);
        }
        return null;
    }

    @Override
    public List<T> getEntityListLimit(Wrapper<T> queryWrapper, int limitCount) {
        // 如果是动态join，则调用JoinsBinder
        //		if(queryWrapper instanceof DynamicJoinQueryWrapper){
        //			Pagination pagination = new Pagination();
        //			pagination.setPageIndex(1).setPageSize(limitCount);
        //			return Binder.joinQueryList((DynamicJoinQueryWrapper)queryWrapper, entityClass,
        // pagination);
        //		}
        Page<T> page = new Page<>(1, limitCount);
        page.setSearchCount(false);
        page = super.page(page, queryWrapper);
        return page.getRecords();
    }
}
