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

package com.kuma.boot.data.mybatis.mybatisplus.aggregate.query.code;

import cn.hutool.core.util.StrUtil;

/** 匹配条件类型 */
public enum CompareTypeEnum {
    GT("gt", ">", "大于"),

    GE("ge", ">=", "大于等于"),

    LT("lt", "<", "小于"),

    LE("le", "<=", "小于等于"),

    EQ("eq", "=", "等于"),

    NE("ne", "!=", "不等于"),

    IN("in", "IN", "包含"),

    NOT_IN("not_in", "NOT_IN", "不包含"),

    BETWEEN("between", "BETWEEN", "在之间"),

    NOT_BETWEEN("not_between", "NOT_BETWEEN", "不在之间"),

    LIKE("like", "LIKE", "全模糊匹配"),

    NOT_LIKE("not_like", "NOT_LIKE", "全模糊不匹配"),

    LIKE_LEFT("like_left", "LIKE_LEFT", "左模糊"),

    LIKE_RIGHT("like_right", "LIKE_RIGHT", "右模糊"),

    IS_NULL("is_null", "IS_NULL", "为空"),

    NOT_NULL("not_null", "NOT_NULL", "不为空");

    private final String code;
    private final String value;
    private final String msg;

    public static CompareTypeEnum getByCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        for (CompareTypeEnum val : values()) {
            if (val.getCode().equals(code)) {
                return val;
            }
        }
        return null;
    }

    CompareTypeEnum(String code, String value, String msg) {
        this.code = code;
        this.value = value;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }
}
