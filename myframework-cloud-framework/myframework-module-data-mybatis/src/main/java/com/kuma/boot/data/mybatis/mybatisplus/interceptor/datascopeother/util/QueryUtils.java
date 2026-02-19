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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.annotation.Query;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.annotation.QueryOrder;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.annotation.QuerySort;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.annotation.SelectColumn;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.enums.ExpressionEnum;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import cn.hutool.core.util.StrUtil;

/**
 * QueryUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class QueryUtils {

    public static <Q, T> QueryWrapper<T> queryWrapperHandler( Q q ) {
        try {
            Class<?> qClass = q.getClass();

            Field[] fields = qClass.getDeclaredFields();

            QueryWrapper<T> queryWrapper = new QueryWrapper<>();

            Map<String, Field[]> betweenFieldMap = new HashMap<>();

            // 处理@SelectColumn
            SelectColumn selectColumn = qClass.getAnnotation(SelectColumn.class);
            if (selectColumn != null
                    && selectColumn.value() != null
                    && selectColumn.value().length > 0) {
                String[] strings = selectColumn.value();
                for (int i = 0; i < strings.length; i++) {
                    strings[i] = StrUtil.toUnderlineCase(strings[i]);
                }
                queryWrapper.select(strings);
            }

            String sortColumn = "";
            String sortOrder = "";

            for (Field field : fields) {
                // if (isBusinessField(field.getName())) {
                field.setAccessible(true);
                Object value = field.get(q);

                // 判断该属性是否存在值
                if (Objects.isNull(value)
                        || String.valueOf(value).equals("null")
                        || value.equals("")) {
                    continue;
                }

                // FIXME 存在bug，应该在判空前执行
                // 是否存在注解@QuerySort
                QuerySort querySort = field.getDeclaredAnnotation(QuerySort.class);
                if (querySort != null) {
                    String paramValue = (String) field.get(q);
                    sortColumn = paramValue.isEmpty() ? querySort.value() : paramValue;
                }

                // 是否存在注解@QueryOrder
                QueryOrder queryOrder = field.getDeclaredAnnotation(QueryOrder.class);
                if (queryOrder != null) {
                    String paramValue = (String) field.get(q);
                    sortOrder = paramValue.isEmpty() ? queryOrder.value() : paramValue;
                }

                // 是否存在注解@Query
                Query query = field.getDeclaredAnnotation(Query.class);
                if (query == null) {
                    continue;
                }

                String columnName =
                        StrUtil.isBlank(query.column())
                                ? StrUtil.toUnderlineCase(field.getName())
                                : query.column();

                if (query.expression().equals(ExpressionEnum.EQ)) {
                    queryWrapper.eq(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.NE)) {
                    queryWrapper.ne(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.LIKE)) {
                    queryWrapper.like(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.GT)) {
                    queryWrapper.gt(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.GE)) {
                    queryWrapper.ge(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.LT)) {
                    queryWrapper.lt(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.LE)) {
                    queryWrapper.le(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.IN)) {
                    queryWrapper.in(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.NOT_IN)) {
                    queryWrapper.notIn(columnName, value);
                } else if (query.expression().equals(ExpressionEnum.IS_NULL)) {
                    queryWrapper.isNull(columnName);
                } else if (query.expression().equals(ExpressionEnum.NOT_NULL)) {
                    queryWrapper.isNotNull(columnName);
                } else if (query.expression().equals(ExpressionEnum.BETWEEN)) {
                    if (betweenFieldMap.containsKey(columnName)) {
                        Field[] f = betweenFieldMap.get(columnName);
                        Field[] tempList = new Field[2];
                        tempList[0] = f[0];
                        tempList[1] = field;
                        betweenFieldMap.put(columnName, tempList);
                    } else {
                        betweenFieldMap.put(columnName, new Field[]{field});
                    }
                }
            }
            // }

            Set<String> keySet = betweenFieldMap.keySet();
            for (String key : keySet) {
                // 已在编译时做了相关校验，在此无须做重复且耗时的校验
                Field[] itemFieldList = betweenFieldMap.get(key);
                if (itemFieldList.length != 2) {
                    throw new IllegalArgumentException("查询参数数量对应异常");
                }

                Field field1 = itemFieldList[0];
                Field field2 = itemFieldList[1];

                Query query1 = field1.getDeclaredAnnotation(Query.class);

                if (field1.get(q) instanceof Date) {
                    if (query1.left()) {
                        queryWrapper.apply(
                                "date_format(" + key + ",'%y%m%d') >= date_format({0},'%y%m%d')",
                                field1.get(q));
                        queryWrapper.apply(
                                "date_format(" + key + ",'%y%m%d') <= date_format({0},'%y%m%d')",
                                field2.get(q));
                    } else {
                        queryWrapper.apply(
                                "date_format(" + key + ",'%y%m%d') <= date_format({0},'%y%m%d')",
                                field1.get(q));
                        queryWrapper.apply(
                                "date_format(" + key + ",'%y%m%d') >= date_format({0},'%y%m%d')",
                                field2.get(q));
                    }
                } else {
                    if (query1.left()) {
                        queryWrapper.between(key, field1.get(q), field2.get(q));
                    } else {
                        queryWrapper.between(key, field2.get(q), field1.get(q));
                    }
                }
            }

            if (sortOrder.equalsIgnoreCase("desc")) {
                queryWrapper.orderByDesc(
                        StrUtil.isNotBlank(sortColumn), StrUtil.toUnderlineCase(sortColumn));
            } else {
                queryWrapper.orderByAsc(
                        StrUtil.isNotBlank(sortColumn), StrUtil.toUnderlineCase(sortColumn));
            }

            return queryWrapper;
        } catch (Exception e) {
            LogUtils.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
