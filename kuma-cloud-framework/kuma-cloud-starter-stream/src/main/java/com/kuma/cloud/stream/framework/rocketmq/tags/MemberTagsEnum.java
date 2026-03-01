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

package com.kuma.cloud.stream.framework.rocketmq.tags;

/** 会员操作枚举 */
public enum MemberTagsEnum {
    /** 会员注册 */
    MEMBER_REGISTER("会员注册"),
    /** 会员注册 */
    MEMBER_LOGIN("会员登录"),
    /** 会员签到 */
    MEMBER_SING("会员签到"),
    /** 会员提现 */
    MEMBER_WITHDRAWAL("会员提现"),
    /** 会员积分变动 */
    MEMBER_POINT_CHANGE("会员积分变动");

    private final String description;

    MemberTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
