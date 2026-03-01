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

package com.kuma.boot.security.spring.event;

import com.kuma.boot.security.spring.event.domain.DialogueMessage;
import java.time.Clock;

/**
 * <p>本地发送对话消息事件
 *
 * @author : gengwei.zheng
 * @since : 2023/3/11 18:40
 */
public class LocalSendDialogueMessageEvent extends com.kuma.boot.security.spring.event.LocalApplicationEvent<DialogueMessage> {

    public LocalSendDialogueMessageEvent(DialogueMessage data) {
        super(data);
    }

    public LocalSendDialogueMessageEvent(DialogueMessage data, Clock clock) {
        super(data, clock);
    }
}
