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

package com.kuma.boot.core.runtime.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 服务启动失败事件
 *
 * @author kuma
 * @version 2023.07
 * @see ApplicationListener
 * @since 2023-07-28 17:23:25
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        event.getException().printStackTrace();

        LogUtils.error("系统启动失败......", event.getException());

        eventCallback(event);
    }

    /**
     * 监听器具体的业务逻辑
     */
    protected void eventCallback(ApplicationFailedEvent event) {}
}
