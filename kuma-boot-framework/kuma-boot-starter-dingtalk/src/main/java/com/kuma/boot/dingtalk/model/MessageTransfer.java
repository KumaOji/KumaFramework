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

import com.kuma.boot.dingtalk.entity.MsgType;

import java.util.Map;

/**
 * MessageTransfer
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:23:26
 */
public interface MessageTransfer {

    /**
     * 转换Dinger消息体发送内容
     *
     * @param dingerDefinition dingerDefinition Dinger消息体定义
     * @param params params 变量列表
     * @return Message 消息内容
     */
    MsgType transfer(DingerDefinition dingerDefinition, Map<String, Object> params);
}
