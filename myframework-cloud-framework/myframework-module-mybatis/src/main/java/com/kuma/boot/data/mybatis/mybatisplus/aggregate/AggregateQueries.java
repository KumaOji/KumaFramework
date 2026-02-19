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

package com.kuma.boot.data.mybatis.mybatisplus.aggregate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Field;
import cn.hutool.core.util.StrUtil;

/**
 * <p>
 * 泛型说明：
 *     <ul>
 *         <li>T：直接性查询条件对象</li>
 *         <li>C：基本查询条件对象</li>
 *         <li>R：模糊查询条件对象</li>
 *         <li>泛型对象可以为空，为空时不进行查询</li>
 *         <li>泛型对象必须是一个Bean</li>
 *         <li>泛型对象的属性<b>必须是基本数据类型</b></li>
 *     </ul>
 * </p>
 * <p>
 * 聚合查询对象
 */
// @Data
// @ApiModel(value = "聚合查询对象")
public class AggregateQueries<T, C, R> {

    /**
     * 直接性查询条件对象(T是一个Bean)
     * <p>
     * 直接性查询对象如果存在，<b>模块查询条件直接失效</b>，场景如：<br/> <b>ID直接查询</b>、<b>手机号直接查询</b>
     * </p>
     */
    //	@ApiModelProperty(value = "直接性查询条件对象")
    private T queries;

    /**
     * 分页信息对象
     * <p>
     * 分页对象包含分页信息，<b>分页信息必须存在</b>，场景如：<br/> <b>分页查询</b>
     * </p>
     */
    //	@ApiModelProperty(value = "分页信息对象")
    private com.kuma.boot.data.mybatis.mybatisplus.aggregate.PaginationDTO pagination;

    /**
     * 基本查询条件对象(C是一个Bean)
     * <p>
     * 基本查询对象与直接性查询可以同时存在，<b>基本查询条件对象的查询条件会与直接性查询条件对象的查询条件进行组合</b>，场景如：<br/>
     * <b>直接性查询ID为10001的用户</b>，<b>基本性查询状态为true的用户</b>，结合后的查询条件为：<br/>
     * <b>查询ID为10001且状态为true的用户</b>
     * </p>
     */
    //	@ApiModelProperty(value = "基本查询条件对象")
    private C condition;

    /**
     * 模糊查询条件对象(R是一个Bean)
     * <p>
     * 模糊查询与直接性条件查询互斥，与基本查询条件对象互补，<b>模糊查询条件对象的查询条件会与基本查询条件对象的查询条件进行组合</b>，场景如：<br/>
     * <b>基本性查询状态为true的用户</b>，<b>模糊性查询用户名为张三的用户</b>，结合后的查询条件为：<br/>
     * <b>查询状态为true且用户名包含张三的用户</b>
     * </p>
     */
    //	@ApiModelProperty(value = "模糊查询条件对象")
    private R fuzzyQueries;

    /**
     * 排序字段
     * <p>
     * 排序字段可以为空，为空时不进行排序
     * </p>
     */
    //	@ApiModelProperty(value = "排序字段")
    private String sortField;

    /**
     * 排序方式
     * <p>
     * 排序方式可以为空，为空时默认为升序，0：升序，1：降序
     * </p>
     */
    //	@ApiModelProperty(value = "排序方式")
    private Integer sortType;

    /**
     * 是否存在直接性查询条件对象
     *
     * @return boolean true：存在，false：不存在
     */
    public boolean hasQueries() {
        return queries != null;
    }

    /**
     * 是否存在分页信息对象
     *
     * @return boolean true：存在，false：不存在
     */
    public boolean hasPagination() {
        return pagination != null;
    }

    /**
     * 是否存在基本查询条件对象
     *
     * @return boolean true：存在，false：不存在
     */
    public boolean hasCondition() {
        return condition != null;
    }

    /**
     * 是否存在模糊查询条件对象
     *
     * @return boolean true：存在，false：不存在
     */
    public boolean hasFuzzyQueries() {
        return fuzzyQueries != null;
    }

    /**
     * 是否存在排序字段
     *
     * @return boolean true：存在，false：不存在
     */
    public boolean hasSortField() {
        return sortField != null;
    }

    /**
     * 是否存在排序方式
     *
     * @return boolean true：存在，false：不存在
     */
    public boolean hasSortType() {
        return sortType != null;
    }

    /**
     * 聚合查询对象拼接
     *
     * @param queries   查询对象
     * @param aggregate 聚合查询对象
     * @return {@link QueryWrapper }<{@link Q }>
     * @since 2023-07-04 08:48:35
     */
    public static <Q, T, C, R> QueryWrapper<Q> splicingAggregateQueries(
            QueryWrapper<Q> queries, AggregateQueries<T, C, R> aggregate) {
        if (aggregate.hasQueries()) {
            splicingQueries(queries, aggregate.getQueries());
        }
        if (aggregate.hasCondition()) {
            splicingQueries(queries, aggregate.getCondition());
        }
        if (aggregate.hasFuzzyQueries() && !aggregate.hasQueries()) {
            splicingFuzzyQueries(queries, aggregate.getFuzzyQueries());
        }
        if (aggregate.hasSortField()) {
            aggregate.setSortType(aggregate.hasSortType() ? aggregate.getSortType() : 0);
            applySort(queries, aggregate.getSortField(), aggregate.getSortType());
        }
        return queries;
    }

    /**
     * 聚合查询对象拼接
     *
     * @param queries 查询对象
     * @param obj     聚合查询属性对象
     * @return {@link QueryWrapper }<{@link Q }>
     * @since 2023-07-04 08:49:46
     */
    public static <Q> QueryWrapper<Q> splicingQueries(QueryWrapper<Q> queries, Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String underlineCase = StrUtil.toUnderlineCase(field.getName());
            try {
                if (field.get(obj) != null) {
                    queries.eq(underlineCase, field.get(obj));
                }
            } catch (IllegalAccessException e) {
                LogUtils.error(e);
            }
        }
        return queries;
    }

    /**
     * 模糊查询对象拼接
     *
     * @param queries 查询对象
     * @param obj     模糊查询属性对象
     * @return 查询对象
     */
    public static <Q> QueryWrapper<Q> splicingFuzzyQueries(QueryWrapper<Q> queries, Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String underlineCase = StrUtil.toUnderlineCase(field.getName());
            try {
                if (field.get(obj) != null) {
                    queries.like(underlineCase, field.get(obj));
                }
            } catch (IllegalAccessException e) {
                LogUtils.error(e);
            }
        }
        return queries;
    }

    /**
     * 排序
     *
     * @param wrapper   查询对象
     * @param sortField 排序字段
     * @param sortType  排序类型
     */
    private static <Q> void applySort(QueryWrapper<Q> wrapper, String sortField, int sortType) {
        String field = StrUtil.toUnderlineCase(sortField);
        if (sortType == 1) {
            wrapper.orderByDesc(field);
        } else {
            wrapper.orderByAsc(field);
        }
    }

    public T getQueries() {
        return queries;
    }

    public void setQueries(T queries) {
        this.queries = queries;
    }

    public com.kuma.boot.data.mybatis.mybatisplus.aggregate.PaginationDTO getPagination() {
        return pagination;
    }

    public void setPagination(com.kuma.boot.data.mybatis.mybatisplus.aggregate.PaginationDTO pagination) {
        this.pagination = pagination;
    }

    public C getCondition() {
        return condition;
    }

    public void setCondition(C condition) {
        this.condition = condition;
    }

    public R getFuzzyQueries() {
        return fuzzyQueries;
    }

    public void setFuzzyQueries(R fuzzyQueries) {
        this.fuzzyQueries = fuzzyQueries;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }
}
