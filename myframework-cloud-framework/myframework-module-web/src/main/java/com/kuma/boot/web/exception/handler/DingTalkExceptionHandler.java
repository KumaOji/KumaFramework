/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.dingtalk.entity.DingerRequest
 *  com.kuma.boot.dingtalk.entity.DingerResponse
 *  com.kuma.boot.dingtalk.enums.MessageSubType
 *  com.kuma.boot.dingtalk.model.DingerSender
 */
package com.kuma.boot.web.exception.handler;

import com.kuma.boot.dingtalk.entity.DingerRequest;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.model.DingerSender;
import com.kuma.boot.web.exception.domain.ExceptionMessage;
import com.kuma.boot.web.exception.domain.ExceptionNoticeResponse;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;

public class DingTalkExceptionHandler
extends AbstractExceptionHandler {
    private final DingerSender dingerSender;

    public DingTalkExceptionHandler(ExceptionHandleProperties config, DingerSender sender, String applicationName) {
        super(config, applicationName);
        this.dingerSender = sender;
    }

    @Override
    public ExceptionNoticeResponse send(ExceptionMessage sendMessage) {
        DingerResponse dingerResponse = this.dingerSender.send(MessageSubType.TEXT, DingerRequest.request((String)sendMessage.toString()));
        ExceptionNoticeResponse response = new ExceptionNoticeResponse();
        response.setErrMsg(dingerResponse.getData());
        response.setSuccess("200".equals(dingerResponse.getCode()));
        return response;
    }

    @Override
    protected void initThread() {
        this.setName("kmc-dingtalk-exception-task");
        this.start();
    }
}

