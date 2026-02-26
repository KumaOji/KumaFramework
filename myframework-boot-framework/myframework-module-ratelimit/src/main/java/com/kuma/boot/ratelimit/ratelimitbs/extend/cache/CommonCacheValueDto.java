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

package com.kuma.boot.ratelimit.ratelimitbs.extend.cache;

import java.io.Serializable;

public class CommonCacheValueDto implements Serializable {

    /**
     * 对应的值
     */
    private String value;

    /**
     * 对应的过期时间
     */
    private Long expireTime;

    public static CommonCacheValueDto of(String value, Long expireTime) {
        CommonCacheValueDto dto = new CommonCacheValueDto();
        dto.setValue(value);

        // 设置过期时间
        if (expireTime != null && expireTime > 0) {
            dto.setExpireTime(expireTime);
        }

        return dto;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "CommonCacheValueDto{" + "value='" + value + '\'' + ", expireTime=" + expireTime + '}';
    }
}
