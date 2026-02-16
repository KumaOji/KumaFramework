/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.mybatis.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.kuma.boot.common.model.request.BasePageQuery;

import java.util.Collection;

/**
 * MyBatis-Plus 常用工具类
 *
 * @author kuma
 */
public final class MybatisUtils {

    private MybatisUtils() {
    }

    /**
     * 构建 in 条件，空集合不添加条件
     */
    public static <T, R> LambdaQueryWrapper<T> inIfNotEmpty(LambdaQueryWrapper<T> wrapper,
                                                            SFunction<T, R> column, Collection<R> values) {
        if (values != null && !values.isEmpty()) {
            wrapper.in(column, values);
        }
        return wrapper;
    }

    /**
     * 构建 like 条件，空字符串不添加条件
     */
    public static <T> LambdaQueryWrapper<T> likeIfNotBlank(LambdaQueryWrapper<T> wrapper,
                                                            SFunction<T, ?> column, String value) {
        if (value != null && !value.isBlank()) {
            wrapper.like(column, value);
        }
        return wrapper;
    }

    /**
     * 构建 eq 条件，null 不添加条件
     */
    public static <T, R> LambdaQueryWrapper<T> eqIfNotNull(LambdaQueryWrapper<T> wrapper,
                                                           SFunction<T, R> column, R value) {
        if (value != null) {
            wrapper.eq(column, value);
        }
        return wrapper;
    }

    /**
     * 构建 between 条件，起止值都非空才添加
     */
    public static <T, R extends Comparable<R>> LambdaQueryWrapper<T> betweenIfPresent(
            LambdaQueryWrapper<T> wrapper, SFunction<T, R> column, R start, R end) {
        if (start != null && end != null) {
            wrapper.between(column, start, end);
        }
        return wrapper;
    }

    /**
     * 从 BasePageQuery 获取 offset，用于原生 SQL 分页
     */
    public static long offset(BasePageQuery<?> query) {
        return query != null ? query.offset() : 0;
    }

    /**
     * 从 BasePageQuery 获取 limit
     */
    public static int limit(BasePageQuery<?> query) {
        if (query == null || query.getPageSize() == null) {
            return 10;
        }
        return query.getPageSize();
    }
}
