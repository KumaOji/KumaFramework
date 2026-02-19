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

package com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.code;

import cn.hutool.core.util.StrUtil;

/** 参数类型 */
public enum ParamTypeEnum {

    /** 数字 */
    NUMBER("number", "数字"),

    /** 字符串 */
    STRING("string", "字符串"),

    /** 布尔 */
    BOOLEAN("boolean", "布尔"),

    /** 日期 */
    DATE("date", "日期"),

    /** 时间 */
    TIME("time", "时间"),

    /** 日期时间 */
    DATE_TIME("date_time", "日期时间"),

    /** 列表 */
    LIST("list", "列表");

    /** 类型编码 */
    private final String code;

    /** 类型名称 */
    private final String name;

    public static ParamTypeEnum getByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        for (ParamTypeEnum val : values()) {
            if (val.getCode().equals(code)) {
                return val;
            }
        }
        return null;
    }

    ParamTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
