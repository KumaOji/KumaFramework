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

package com.kuma.boot.web.support.holidays.core;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 日期类型，工作日对应结果为 0, 休息日对应结果为 1, 节假日对应的结果为 2；
 */
public enum DaysType {

    /**
     * 工作日
     */
    WEEKDAYS((byte) 0),
    /**
     * 休息日
     */
    REST_DAYS((byte) 1),
    /**
     * 节假日
     */
    HOLIDAYS((byte) 2);

    @JsonValue private final byte type;

    DaysType(byte type) {
        this.type = type;
    }

    /**
     * 将 type 转换成枚举
     *
     * @param type type
     * @return DaysType
     */
    public static DaysType from(byte type) {
        switch (type) {
            case 0:
                return WEEKDAYS;
            case 1:
                return REST_DAYS;
            case 2:
                return HOLIDAYS;
            default:
                throw new IllegalArgumentException("未知的 DaysType:" + type);
        }
    }

    public byte getType() {
        return this.type;
    }
}
