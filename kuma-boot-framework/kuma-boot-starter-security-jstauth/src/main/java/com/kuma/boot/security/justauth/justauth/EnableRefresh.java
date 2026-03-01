/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth;

/**
 * 第三方服务商是否支持 refresh token 枚举
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/16 21:47
 */
public enum EnableRefresh {
    /**
     * 不支持 refresh token
     */
    NO(0, "不支持"),
    /**
     * 支持 refresh token
     */
    YES(1, "支持");

    private Integer code;
    private String msg;

    EnableRefresh(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
