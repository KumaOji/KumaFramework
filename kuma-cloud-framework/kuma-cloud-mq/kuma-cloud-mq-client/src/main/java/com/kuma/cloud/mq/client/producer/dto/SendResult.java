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

package com.kuma.cloud.mq.client.producer.dto;

import com.kuma.cloud.mq.client.producer.constant.SendStatus;

/**
 * @author kuma
 * @since 2024.05
 */
public class SendResult {

    /**
     * 消息唯一标识
     */
    private String messageId;

    /**
     * 发送状态
     */
    private SendStatus status;

    public static SendResult of(String messageId, SendStatus status) {
        SendResult result = new SendResult();
        result.setMessageId(messageId);
        result.setStatus(status);

        return result;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public SendStatus getStatus() {
        return status;
    }

    public void setStatus(SendStatus status) {
        this.status = status;
    }
}
