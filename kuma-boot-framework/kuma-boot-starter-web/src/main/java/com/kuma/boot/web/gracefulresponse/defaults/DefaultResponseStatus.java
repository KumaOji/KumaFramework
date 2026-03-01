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

package com.kuma.boot.web.gracefulresponse.defaults;

import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;

/**
 * 默认的ResponseStatus实现
 */
public class DefaultResponseStatus implements ResponseStatus {

    /**
     * 响应码.
     */
    private String code;

    /**
     * 响应信息.
     */
    private String msg;

    public DefaultResponseStatus() {}

    /**
     * 通过响应码和响应信息构造枚举.
     *
     * @param code 响应码
     * @param msg  响应信息
     */
    public DefaultResponseStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
