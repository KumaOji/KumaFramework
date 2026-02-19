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

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;
import static cn.hutool.core.date.DatePattern.NORM_TIME_PATTERN;

import cn.hutool.core.date.DateUtil;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.code.ParamTypeEnum;
import com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.entity.QueryParam;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import cn.hutool.core.util.StrUtil;

/** 参数值转换 */
public class ParamValueTypeConvert {

    /**
     * 查询条件值处理
     *
     * @param queryParam 查询参数
     * @return 处理完的查询条件值
     */
    public static Object initQueryParamValue(QueryParam queryParam) {
        Object paramValue = queryParam.getParamValue();
        // 空值不进行处理
        if (Objects.isNull(paramValue)) {
            return null;
        }
        // 未传入参数类型原样返回
        if (StrUtil.isBlank(queryParam.getParamType())) {
            return paramValue;
        }
        ParamTypeEnum paramTypeEnum =
                Optional.ofNullable(ParamTypeEnum.getByCode(queryParam.getParamType()))
                        .orElseThrow(() -> new BusinessException("不支持的数据类型"));
        switch (paramTypeEnum) {
            // 原样返回
            case NUMBER:
            case STRING:
            case BOOLEAN:
            case DATE:
            case TIME:
            case DATE_TIME:
                return convertType(paramValue, paramTypeEnum);
            case LIST:
            {
                Collection<?> collection = (Collection<?>) paramValue;
                return collection.stream().map(o -> convertType(o, paramTypeEnum)).toList();
            }
            default:
                return null;
        }
    }

    /**
     * 数据类型解析
     *
     * @param paramValue 参数
     * @param paramTypeEnum 参数类型
     * @return 解析完的数据
     */
    private static Object convertType(Object paramValue, ParamTypeEnum paramTypeEnum) {

        ParamTypeEnum typeEnum =
                Optional.ofNullable(ParamTypeEnum.getByCode(paramTypeEnum.getCode()))
                        .orElseThrow(() -> new BusinessException("不支持的数据类型"));
        switch (typeEnum) {
            // 原样返回
            case NUMBER:
            case STRING:
            case BOOLEAN:
                return paramValue;
            case DATE:
                return DateUtil.parse((String) paramValue, NORM_DATE_PATTERN);
            case TIME:
                return DateUtil.parse((String) paramValue, NORM_TIME_PATTERN);
            case DATE_TIME:
                return DateUtil.parse((String) paramValue, NORM_DATETIME_PATTERN);
            case LIST:
                return paramValue;
            default:
                throw new BusinessException("类型错误");
        }
    }
}
