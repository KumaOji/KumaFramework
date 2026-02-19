/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 */
package com.kuma.boot.web.gracefulresponse.defaults;

import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;

public class DefaultResponseStatusFactoryImpl
implements ResponseStatusFactory {
    @Resource
    private GracefulResponseProperties properties;

    @Override
    public ResponseStatus defaultSuccess() {
        DefaultResponseStatus defaultResponseStatus = new DefaultResponseStatus();
        defaultResponseStatus.setCode(this.properties.getDefaultSuccessCode());
        defaultResponseStatus.setMsg(this.properties.getDefaultSuccessMsg());
        return defaultResponseStatus;
    }

    @Override
    public ResponseStatus defaultError() {
        DefaultResponseStatus defaultResponseStatus = new DefaultResponseStatus();
        defaultResponseStatus.setCode(this.properties.getDefaultErrorCode());
        defaultResponseStatus.setMsg(this.properties.getDefaultErrorMsg());
        return defaultResponseStatus;
    }

    @Override
    public ResponseStatus newInstance(String code, String msg) {
        return new DefaultResponseStatus(code, msg);
    }
}

