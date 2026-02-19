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

package com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.generator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.code.CompareTypeEnum;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.entity.QueryBetweenParam;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.entity.QueryOrder;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.entity.QueryParam;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.entity.QueryParams;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 查询条件生成器
 *
 * <pre code="class">
 *  public Page<Client> supperPage(PageParam pageParam,QueryParams queryParams){
 * QueryWrapper<Client> generator=QueryGenerator.generator(queryParams);
 * Page<Client> mpPage=MpUtil.getMpPage(pageParam,Client.class);
 * return this.page(mpPage,generator);
 * }
 * </pre>
 */
public class QueryGenerator {

    /**
     * 生成查询条件
     *
     * @param queryParams 参数
     * @param <T> 泛型
     * @return 查询器
     */
    public static <T> QueryWrapper<T> generator(QueryParams queryParams) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        // 查询条件
        initQueryParam(queryWrapper, queryParams.getQueryParams());

        // 排序条件
        initQueryOrder(queryWrapper, queryParams.getQueryOrders());

        return queryWrapper;
    }

    /**
     * 组装查询条件
     *
     * @param queryWrapper 查询器
     * @param queryParams 查询参数
     * @param <T> 泛型
     */
    private static <T> void initQueryParam(
            QueryWrapper<T> queryWrapper, List<QueryParam> queryParams) {
        if (CollUtil.isEmpty(queryParams)) {
            return;
        }
        for (QueryParam queryParam : queryParams) {
            // 嵌套条件
            List<QueryParam> nestedParams = queryParam.getNestedParams();

            // 是否拼接or条件
            if (queryParam.isOr() && CollUtil.isEmpty(nestedParams)) {
                queryWrapper.or();
            }

            // 有嵌套查询进行嵌套处理
            if (CollUtil.isEmpty(nestedParams)) {
                // 组装单条查询条件
                initQueryParam(queryWrapper, queryParam);
            } else {
                // 将当前查询条件与嵌套的查询条件组装成一个查询条件
                QueryParam q = new QueryParam();
                q.setParamName(queryParam.getParamName());
                q.setCompareType(queryParam.getCompareType());
                q.setParamType(queryParam.getParamType());
                q.setParamValue(queryParam.getParamValue());
                q.setUnderLine(queryParam.isUnderLine());
                q.setOr(queryParam.isOr());

                nestedParams.add(0, q);
                if (queryParam.isOr()) {
                    queryWrapper.or(wrapper -> initQueryParam(wrapper, nestedParams));
                } else {
                    queryWrapper.and(wrapper -> initQueryParam(wrapper, nestedParams));
                }
            }
        }
    }

    /**
     * 组装单条查询条件
     *
     * @param queryWrapper 查询器
     * @param queryParam 查询参数
     * @param <T> 泛型
     */
    private static <T> void initQueryParam(QueryWrapper<T> queryWrapper, QueryParam queryParam) {

        // 处理查询参数名称
        String paramName = initQueryParamName(queryParam);
        // 处理查询条件值
        Object paramValue = ParamValueTypeConvert.initQueryParamValue(queryParam);

        // 查询匹配类型
        CompareTypeEnum compareTypeEnum =
                Optional.ofNullable(CompareTypeEnum.getByCode(queryParam.getCompareType()))
                        .orElseThrow(() -> new BusinessException("查询匹配类型非法"));
        switch (compareTypeEnum) {
            case GT:
                queryWrapper.gt(paramName, paramValue);
                break;
            case GE:
                queryWrapper.ge(paramName, paramValue);
                break;
            case LT:
                queryWrapper.lt(paramName, paramValue);
                break;
            case LE:
                queryWrapper.le(paramName, paramValue);
                break;
            case EQ:
                queryWrapper.eq(paramName, paramValue);
                break;
            case NE:
                queryWrapper.ne(paramName, paramValue);
                break;
            case IN:
                queryWrapper.in(paramName, (Collection<?>) paramValue);
                break;
            case NOT_IN:
                queryWrapper.notIn(paramName, (Collection<?>) paramValue);
                break;
            case BETWEEN:
            {
                if (Objects.isNull(paramValue)) {
                    throw new BusinessException("between 查询条件为空");
                }
                QueryBetweenParam value = (QueryBetweenParam) paramValue;
                queryWrapper.between(paramName, value.getStart(), value.getEnd());
                break;
            }
            case NOT_BETWEEN:
            {
                if (Objects.isNull(paramValue)) {
                    throw new BusinessException("between 查询条件为空");
                }
                QueryBetweenParam value = (QueryBetweenParam) paramValue;
                queryWrapper.notBetween(paramName, value.getStart(), value.getEnd());
                break;
            }
            case LIKE:
                queryWrapper.like(paramName, paramValue);
                break;
            case NOT_LIKE:
                queryWrapper.notLike(paramName, paramValue);
                break;
            case LIKE_LEFT:
                queryWrapper.likeLeft(paramName, paramValue);
                break;
            case LIKE_RIGHT:
                queryWrapper.likeRight(paramName, paramValue);
                break;
            case IS_NULL:
                queryWrapper.isNull(paramName);
                break;
            case NOT_NULL:
                queryWrapper.isNotNull(paramName);
                break;
            default:
                throw new BusinessException("查询匹配类型非法");
        }
    }

    /**
     * 组装排序条件
     *
     * @param queryWrapper 查询器
     * @param queryOrders 排序条件
     * @param <T> 泛型
     */
    private static <T> void initQueryOrder(
            QueryWrapper<T> queryWrapper, List<QueryOrder> queryOrders) {
        if (CollUtil.isEmpty(queryOrders)) {
            return;
        }
        for (QueryOrder queryOrder : queryOrders) {
            if (queryOrder.isUnderLine()) {
                queryWrapper.orderBy(
                        true,
                        queryOrder.isAsc(),
                        StrUtil.toUnderlineCase(queryOrder.getSortField()));
            } else {
                queryWrapper.orderBy(true, queryOrder.isAsc(), queryOrder.getSortField());
            }
        }
    }

    /**
     * 查询参数名称处理
     *
     * @param queryParam 查询参数
     * @return 处理完的查询参数值
     */
    private static String initQueryParamName(QueryParam queryParam) {
        String paramName = queryParam.getParamName();
        if (queryParam.isUnderLine()) {
            return StrUtil.toUnderlineCase(paramName);
        } else {
            return paramName;
        }
    }
}
