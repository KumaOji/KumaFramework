/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.entity.DingerCallback;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.support.CustomMessage;

/**
 * AbstractDingTalkSender
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:22:05
 */
public abstract class AbstractDingerSender extends DingerHelper implements DingerSender {

    protected DingtalkProperties dingtalkProperties;
    protected DingerManagerBuilder dingTalkManagerBuilder;

    public AbstractDingerSender(DingtalkProperties dingtalkProperties, DingerManagerBuilder dingTalkManagerBuilder) {
        this.dingtalkProperties = dingtalkProperties;
        this.dingTalkManagerBuilder = dingTalkManagerBuilder;
    }

    /**
     * 消息类型校验
     *
     * @param messageSubType 消息类型
     * @return 消息生成器
     */
    protected CustomMessage customMessage(MessageSubType messageSubType) {
        return messageSubType == MessageSubType.TEXT
                ? dingTalkManagerBuilder.getTextMessage()
                : dingTalkManagerBuilder.getMarkDownMessage();
    }

    /**
     * 异常回调
     *
     * @param dingerId dingerId
     * @param message message
     * @param ex ex
     * @param <T> T
     */
    protected <T> void exceptionCallback(String dingerId, T message, DingerException ex) {
        @SuppressWarnings("unchecked")
        DingerCallback dkExCallable = new DingerCallback(dingerId, message, ex);
        dingTalkManagerBuilder.getDingerExceptionCallback().execute(dkExCallable);
    }
}
