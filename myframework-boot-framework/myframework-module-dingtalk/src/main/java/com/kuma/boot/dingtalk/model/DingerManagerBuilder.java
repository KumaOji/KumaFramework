/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.support.CustomMessage;
import com.kuma.boot.dingtalk.support.DingerAsyncCallback;
import com.kuma.boot.dingtalk.support.DingerExceptionCallback;
import com.kuma.boot.dingtalk.support.DingerHttpClient;
import com.kuma.boot.dingtalk.support.DingerIdGenerator;
import com.kuma.boot.dingtalk.support.DingerSignAlgorithm;
import java.util.concurrent.Executor;
import org.springframework.web.client.RestTemplate;

public class DingerManagerBuilder {
    private RestTemplate dingerRestTemplate;
    private DingerExceptionCallback dingerExceptionCallback;
    private CustomMessage textMessage;
    private CustomMessage markDownMessage;
    private DingerSignAlgorithm dingerSignAlgorithm;
    private DingerIdGenerator dingerIdGenerator;
    private Executor dingTalkExecutor;
    private DingerAsyncCallback dingerAsyncCallback;
    private DingerHttpClient dingerHttpClient;

    public DingerManagerBuilder(RestTemplate dingerRestTemplate, DingerExceptionCallback dingerExceptionCallback, CustomMessage textMessage, CustomMessage markDownMessage, DingerSignAlgorithm dingerSignAlgorithm, DingerIdGenerator dingerIdGenerator, Executor dingTalkExecutor, DingerAsyncCallback dingerAsyncCallback, DingerHttpClient dingerHttpClient) {
        this.dingerRestTemplate = dingerRestTemplate;
        this.dingerExceptionCallback = dingerExceptionCallback;
        this.textMessage = textMessage;
        this.markDownMessage = markDownMessage;
        this.dingerSignAlgorithm = dingerSignAlgorithm;
        this.dingerIdGenerator = dingerIdGenerator;
        this.dingTalkExecutor = dingTalkExecutor;
        this.dingerAsyncCallback = dingerAsyncCallback;
        this.dingerHttpClient = dingerHttpClient;
    }

    public DingerManagerBuilder dingerRestTemplate(RestTemplate dingerRestTemplate) {
        if (dingerRestTemplate != null) {
            this.dingerRestTemplate = dingerRestTemplate;
        }
        return this;
    }

    public DingerManagerBuilder dingerExceptionCallback(DingerExceptionCallback dingerExceptionCallback) {
        if (dingerExceptionCallback != null) {
            this.dingerExceptionCallback = dingerExceptionCallback;
        }
        return this;
    }

    public DingerManagerBuilder textMessage(CustomMessage textMessage) {
        if (textMessage != null) {
            this.textMessage = textMessage;
        }
        return this;
    }

    public DingerManagerBuilder markDownMessage(CustomMessage markDownMessage) {
        if (markDownMessage != null) {
            this.markDownMessage = markDownMessage;
        }
        return this;
    }

    public DingerManagerBuilder dingerSignAlgorithm(DingerSignAlgorithm dingerSignAlgorithm) {
        if (dingerSignAlgorithm != null) {
            this.dingerSignAlgorithm = dingerSignAlgorithm;
        }
        return this;
    }

    public DingerManagerBuilder dingerIdGenerator(DingerIdGenerator dingerIdGenerator) {
        if (dingerIdGenerator != null) {
            this.dingerIdGenerator = dingerIdGenerator;
        }
        return this;
    }

    public DingerManagerBuilder dingTalkExecutor(Executor dingTalkExecutor) {
        if (dingTalkExecutor != null) {
            this.dingTalkExecutor = dingTalkExecutor;
        }
        return this;
    }

    public DingerManagerBuilder dingerAsyncCallback(DingerAsyncCallback dingerAsyncCallback) {
        if (dingerAsyncCallback != null) {
            this.dingerAsyncCallback = dingerAsyncCallback;
        }
        return this;
    }

    public RestTemplate getDingerRestTemplate() {
        return this.dingerRestTemplate;
    }

    public void setDingerRestTemplate(RestTemplate dingerRestTemplate) {
        this.dingerRestTemplate = dingerRestTemplate;
    }

    public DingerExceptionCallback getDingerExceptionCallback() {
        return this.dingerExceptionCallback;
    }

    public void setDingerExceptionCallback(DingerExceptionCallback dingerExceptionCallback) {
        this.dingerExceptionCallback = dingerExceptionCallback;
    }

    public CustomMessage getTextMessage() {
        return this.textMessage;
    }

    public void setTextMessage(CustomMessage textMessage) {
        this.textMessage = textMessage;
    }

    public CustomMessage getMarkDownMessage() {
        return this.markDownMessage;
    }

    public void setMarkDownMessage(CustomMessage markDownMessage) {
        this.markDownMessage = markDownMessage;
    }

    public DingerSignAlgorithm getDingerSignAlgorithm() {
        return this.dingerSignAlgorithm;
    }

    public void setDingerSignAlgorithm(DingerSignAlgorithm dingerSignAlgorithm) {
        this.dingerSignAlgorithm = dingerSignAlgorithm;
    }

    public DingerIdGenerator getDingerIdGenerator() {
        return this.dingerIdGenerator;
    }

    public void setDingerIdGenerator(DingerIdGenerator dingerIdGenerator) {
        this.dingerIdGenerator = dingerIdGenerator;
    }

    public Executor getDingTalkExecutor() {
        return this.dingTalkExecutor;
    }

    public void setDingTalkExecutor(Executor dingTalkExecutor) {
        this.dingTalkExecutor = dingTalkExecutor;
    }

    public DingerAsyncCallback getDingerAsyncCallback() {
        return this.dingerAsyncCallback;
    }

    public void setDingerAsyncCallback(DingerAsyncCallback dingerAsyncCallback) {
        this.dingerAsyncCallback = dingerAsyncCallback;
    }

    public DingerHttpClient getDingerHttpClient() {
        return this.dingerHttpClient;
    }

    public void setDingerHttpClient(DingerHttpClient dingerHttpClient) {
        this.dingerHttpClient = dingerHttpClient;
    }
}

