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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope;

import com.kuma.boot.common.enums.base.CommonEnum;
import java.util.stream.Stream;

/**
 * 数据权限类型
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:40:32
 */
public enum DataScopeTypeEnum implements CommonEnum {

    /** ALL=5全部 */
    ALL(5, "全部"),
    /** THIS_LEVEL=4本级 */
    THIS_LEVEL(4, "本级"),
    /** THIS_LEVEL_CHILDREN=3本级以及子级 */
    THIS_LEVEL_CHILDREN(3, "本级以及子级"),
    /** CUSTOMIZE=2自定义 */
    CUSTOMIZE(2, "自定义"),
    /** SELF=1个人 */
    SELF(1, "个人");

    private final int code;
    private final String desc;

    DataScopeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DataScopeTypeEnum match(String code, DataScopeTypeEnum def) {
        return Stream.of(values())
                .parallel()
                .filter((item) -> item.name().equalsIgnoreCase(code))
                .findAny()
                .orElse(def);
    }

    public static DataScopeTypeEnum match(Integer code, DataScopeTypeEnum def) {
        return Stream.of(values())
                .parallel()
                .filter((item) -> code != null && item.getCode() == code)
                .findAny()
                .orElse(def);
    }

    public static DataScopeTypeEnum get(String code) {
        return match(code, null);
    }

    public static DataScopeTypeEnum get(Integer code) {
        return match(code, null);
    }

    public boolean eq(final DataScopeTypeEnum code) {
        return code != null;
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
