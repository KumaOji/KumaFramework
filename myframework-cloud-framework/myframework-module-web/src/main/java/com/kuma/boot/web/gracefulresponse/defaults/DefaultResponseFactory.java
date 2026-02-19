/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.web.gracefulresponse.defaults;

import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class DefaultResponseFactory
implements ResponseFactory {
    private final Logger logger = LoggerFactory.getLogger(DefaultResponseFactory.class);
    private static final Integer RESPONSE_STYLE_0 = 0;
    private static final Integer RESPONSE_STYLE_1 = 1;
    @Resource
    private ResponseStatusFactory responseStatusFactory;
    @Resource
    private GracefulResponseProperties properties;

    @Override
    public Response newEmptyInstance() {
        try {
            String responseClassFullName = this.properties.getResponseClassFullName();
            if (StringUtils.hasLength((String)responseClassFullName)) {
                Object newInstance = Class.forName(responseClassFullName).getConstructor(new Class[0]).newInstance(new Object[0]);
                return (Response)newInstance;
            }
            return this.generateDefaultResponse();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response generateDefaultResponse() {
        Integer responseStyle = this.properties.getResponseStyle();
        if (Objects.isNull(responseStyle) || RESPONSE_STYLE_0.equals(responseStyle)) {
            return new DefaultResponseImplStyle0();
        }
        if (RESPONSE_STYLE_1.equals(responseStyle)) {
            return new DefaultResponseImplStyle1();
        }
        this.logger.error("\u4e0d\u652f\u6301\u7684Response style\u7c7b\u578b,responseStyle={}", (Object)responseStyle);
        throw new IllegalArgumentException("\u4e0d\u652f\u6301\u7684Response style\u7c7b\u578b");
    }

    @Override
    public Response newInstance(ResponseStatus responseStatus) {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatus);
        return bean;
    }

    @Override
    public Response newSuccessInstance() {
        Response emptyInstance = this.newEmptyInstance();
        emptyInstance.setStatus(this.responseStatusFactory.defaultSuccess());
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
        bean.setStatus(this.responseStatusFactory.defaultError());
        return bean;
    }
}

