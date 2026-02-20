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
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 提供的默认的ResponseBeanFactory实现.
 */
public class DefaultResponseFactory implements ResponseFactory {

    private final Logger logger = LoggerFactory.getLogger(DefaultResponseFactory.class);

    private static final Integer RESPONSE_STYLE_0 = 0;

    private static final Integer RESPONSE_STYLE_1 = 1;

    @Resource private ResponseStatusFactory responseStatusFactory;

    @Resource private GracefulResponseProperties properties;

    @Override
    public Response newEmptyInstance() {
        try {
            String responseClassFullName = properties.getResponseClassFullName();

            // 配置了Response的全限定名，即自定义了Response，用配置的进行返回
            if (StringUtils.hasLength(responseClassFullName)) {
                Object newInstance =
                        Class.forName(responseClassFullName).getConstructor().newInstance();
                return (Response) newInstance;
            } else {
                // 没有配Response的全限定名，则创建DefaultResponse
                return generateDefaultResponse();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response generateDefaultResponse() {
        Integer responseStyle = properties.getResponseStyle();
        if (Objects.isNull(responseStyle) || RESPONSE_STYLE_0.equals(responseStyle)) {
            return new DefaultResponseImplStyle0();
        } else if (RESPONSE_STYLE_1.equals(responseStyle)) {
            return new DefaultResponseImplStyle1();
        } else {
            logger.error("不支持的Response style类型,responseStyle={}", responseStyle);
            throw new IllegalArgumentException("不支持的Response style类型");
        }
    }

    @Override
    public Response newInstance( ResponseStatus responseStatus) {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatus);
        return bean;
    }

    @Override
    public Response newSuccessInstance() {
        Response emptyInstance = this.newEmptyInstance();
        emptyInstance.setStatus(responseStatusFactory.defaultSuccess());
        return emptyInstance;
    }

    @Override
    public Response newSuccessInstance(Object payload) {
        Response bean = this.newSuccessInstance();
        bean.setPayload(payload);
        return bean;
    }

    @Override
    public Response newFailInstance() {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatusFactory.defaultError());
        return bean;
    }
}
