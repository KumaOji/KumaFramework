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

package com.kuma.boot.i18n.event;

import com.kuma.boot.i18n.message.I18nMessage;
import java.util.List;
import org.springframework.context.ApplicationEvent;

/**
 * 国际化消息批量创建事件.
 *
 * <p>业务层发布此事件，Listener 监听并持久化 {@link I18nMessage} 列表到数据库或缓存，
 * 实现消息的动态运维。
 *
 * @author kuma
 */
public class I18nMessageCreateEvent extends ApplicationEvent {

    public I18nMessageCreateEvent(List<I18nMessage> i18nMessages) {
        super(i18nMessages);
    }

    @SuppressWarnings("unchecked")
    public List<I18nMessage> getI18nMessages() {
        return (List<I18nMessage>) super.getSource();
    }
}
