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

package com.kuma.boot.web.exception.handler;

import com.kuma.boot.dingtalk.entity.DingerRequest;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.model.DingerSender;
import com.kuma.boot.web.exception.domain.ExceptionMessage;
import com.kuma.boot.web.exception.domain.ExceptionNoticeResponse;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;

/** 钉钉消息通知 */
public class DingTalkExceptionHandler extends com.kuma.boot.web.exception.handler.AbstractExceptionHandler {

    private final DingerSender dingerSender;

    public DingTalkExceptionHandler(
            ExceptionHandleProperties config, DingerSender sender, String applicationName) {
        super(config, applicationName);
        this.dingerSender = sender;
    }

    @Override
    public ExceptionNoticeResponse send( ExceptionMessage sendMessage) {
        DingerResponse dingerResponse =
                dingerSender.send(
                        MessageSubType.TEXT, DingerRequest.request(sendMessage.toString()));
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
