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

import com.kuma.boot.dingtalk.entity.DingerRequest;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;

/**
 * DingTalk Sender
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:23:22
 */
public interface DingerSender {

    /**
     * 发送消息到指定群
     *
     * <pre>
     *     使用配置的默认钉钉机器人, {@link DingtalkProperties#getDefaultDinger()}
     * </pre>
     *
     * @param messageSubType 消息类型{@link MessageSubType}
     * @param request 请求体 {@link DingerRequest}
     * @return 响应报文
     */
    DingerResponse send(MessageSubType messageSubType, DingerRequest request);

    /**
     * 发送消息到指定群
     *
     * @param dingerType Dinger类型 {@link DingerType}
     * @param messageSubType 消息类型{@link MessageSubType}
     * @param request 请求体 {@link DingerRequest}
     * @return 响应报文
     */
    DingerResponse send(DingerType dingerType, MessageSubType messageSubType, DingerRequest request);
}
