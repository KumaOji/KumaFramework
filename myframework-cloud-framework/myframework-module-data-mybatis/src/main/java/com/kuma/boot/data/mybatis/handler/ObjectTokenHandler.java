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

package com.kuma.boot.data.mybatis.handler;

import com.kuma.boot.common.utils.log.LogUtils;

import java.lang.reflect.Field;

import org.apache.ibatis.parsing.TokenHandler;

/**
 * ObjectTokenHandler
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class ObjectTokenHandler implements TokenHandler {

    private final Object obj;

    public ObjectTokenHandler( Object obj ) {
        this.obj = obj;
    }

    @Override
    public String handleToken( String content ) {
        // 根据占位符的内容从对象中获取对应的值
        String value = "'-'";
        try {
            value = getObjectValue(obj, content);
        } catch (Exception e) {
            // 处理异常情况，例如字段不存在等
            LogUtils.error(e);
        }
        return value;
    }

    /**
     * 获取对象中指定字段的值
     */
    private String getObjectValue( Object obj, String fieldPath ) throws Exception {
        // 将字段路径按点号拆分为多个字段名
        String[] fields = fieldPath.split("\\.");
        // 从 obj 对象开始，逐级获取字段值
        Object fieldValue = obj;
        for (String field : fields) {
            // 获取当前字段名对应的字段值
            fieldValue = getFieldValue(fieldValue, field);
            if (fieldValue == null) {
                // 如果字段值为空，则退出循环
                break;
            }
        }
        // 将字段值转换为字符串并返回
        return fieldValue != null ? fieldValue.toString() : "'-'";
    }

    /**
     * 获取对象中指定字段的值
     */
    private Object getFieldValue( Object obj, String fieldName ) throws Exception {
        // 获取字段对象
        Field field = obj.getClass().getDeclaredField(fieldName);
        // 设置字段可访问
        field.setAccessible(true);
        // 获取字段值
        return field.get(obj);
    }
}
