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

import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;

/**
 * 提供的默认的ResponseMetaFactory实现.
 *
 */
public class DefaultResponseStatusFactoryImpl implements ResponseStatusFactory {

    @Resource private GracefulResponseProperties properties;

    @Override
    public ResponseStatus defaultSuccess() {

        com.kuma.boot.web.gracefulresponse.defaults.DefaultResponseStatus defaultResponseStatus = new com.kuma.boot.web.gracefulresponse.defaults.DefaultResponseStatus();
        defaultResponseStatus.setCode(properties.getDefaultSuccessCode());
        defaultResponseStatus.setMsg(properties.getDefaultSuccessMsg());
        return defaultResponseStatus;
    }

    @Override
    public ResponseStatus defaultError() {
        com.kuma.boot.web.gracefulresponse.defaults.DefaultResponseStatus defaultResponseStatus = new com.kuma.boot.web.gracefulresponse.defaults.DefaultResponseStatus();
        defaultResponseStatus.setCode(properties.getDefaultErrorCode());
        defaultResponseStatus.setMsg(properties.getDefaultErrorMsg());
        return defaultResponseStatus;
    }

    @Override
    public ResponseStatus newInstance(String code, String msg) {
        return new com.kuma.boot.web.gracefulresponse.defaults.DefaultResponseStatus(code, msg);
    }
}
