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

package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

/**
 * 用户锁定类型
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:03:32
 */
public enum LockFlagEnum implements CommonEnum {

    /** 锁定 */
    LOCKED(0, "锁定"),
    /** 正常 */
    NORMAL(1, "正常");

    private final int code;

    private final String desc;

    LockFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getNameByCode(int code) {
        for (LockFlagEnum result : LockFlagEnum.values()) {
            if (result.getCode() == code) {
                return result.name().toLowerCase();
            }
        }
        return null;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
