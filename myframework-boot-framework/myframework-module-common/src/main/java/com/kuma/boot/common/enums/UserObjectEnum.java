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
 * 系统用户类型
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:32:26
 */
public enum UserObjectEnum implements CommonEnum {

    /** */
    DEPT(1, "dept", "部门"),
    POSITION(2, "position", "岗位"),
    ROLE(3, "role", "角色"),
    ORG(4, "org", "组织"),
    MANAGER(5, "dataScope", "数据权限");

    private final int code;

    private final String value;

    private final String desc;

    UserObjectEnum(int code, String value, String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getNameByCode(int code) {
        for (UserObjectEnum result : UserObjectEnum.values()) {
            if (result.getCode() == code) {
                return result.name().toLowerCase();
            }
        }
        return null;
    }
}
