/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.support;

import com.kuma.boot.dingtalk.exception.SendMsgException;

import java.util.Map;

public abstract class AbstractDingerHttpClient
implements DingerHttpClient {
    @Override
    public String get(String url) throws SendMsgException {
        return null;
    }

    @Override
    public String get(String url, Map<String, String> headers) throws SendMsgException {
        return null;
    }

    @Override
    public String get(String url, Map<String, String> headers, Map<String, ?> params) throws SendMsgException {
        return null;
    }

    @Override
    public String post(String url, String message) throws SendMsgException {
        return null;
    }

    @Override
    public String post(String url, Map<String, String> headers, String message) throws SendMsgException {
        return null;
    }

    @Override
    public String post(String url, Map<String, String> headers, Map<String, ?> params) throws SendMsgException {
        return null;
    }
}

