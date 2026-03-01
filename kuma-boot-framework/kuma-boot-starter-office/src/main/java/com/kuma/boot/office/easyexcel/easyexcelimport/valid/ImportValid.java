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

package com.kuma.boot.office.easyexcel.easyexcelimport.valid;

import com.kuma.boot.office.easyexcel.easyexcelimport.annotation.ImportFieldValid;
import com.kuma.boot.office.easyexcel.easyexcelimport.model.ImportCommonConstant;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class ImportValid {

    /**
     * 检查导入的必填字段
     *
     * @param object
     * @param errorLog
     */
    public static void validRequireField(Object object, List<String> errorLog) {
        StringBuilder log = new StringBuilder();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 设置可访问属性
            field.setAccessible(Boolean.TRUE);
            Object fieldValue = null;
            boolean isValid = field.isAnnotationPresent(ImportFieldValid.class);
            if (isValid) {
                try {
                    fieldValue = field.get(object);
                } catch (Exception e) {
                    throw new RuntimeException("校验必填字段时出现异常," + e.getMessage());
                }
                // 字符串类型，需要判断为NULL和""的情况
                if (Objects.nonNull(fieldValue)
                        && field.getType().getName().contains(ImportCommonConstant.STRING_TYPE)) {
                    if (StrUtil.isBlank(fieldValue.toString())) {
                        String message =
                                field.getAnnotation(ImportFieldValid.class).message();
                        log.append(message).append(StrUtil.COMMA);
                    }
                } else {
                    if (Objects.isNull(fieldValue)) {
                        String message =
                                field.getAnnotation(ImportFieldValid.class).message();
                        log.append(message).append(StrUtil.COMMA);
                    }
                }
            }
        }
        if (log.length() > ImportCommonConstant.ZERO) {
            errorLog.add(StrUtil.removeSuffix(log.toString(), StrUtil.COMMA));
        }
    }
}
