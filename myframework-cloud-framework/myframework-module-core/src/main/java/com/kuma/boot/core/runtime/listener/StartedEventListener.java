/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

/**
 * 项目启动事件通知
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:01:42
 */
@AutoConfiguration
public class StartedEventListener {

    @Async
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    @EventListener(WebServerInitializedEvent.class)
    public void afterStart(WebServerInitializedEvent event) {
        WebServerApplicationContext context = event.getApplicationContext();
        Environment environment = context.getEnvironment();
        String appName = environment.getRequiredProperty(CommonConstants.SPRING_APP_NAME_KEY);
        int localPort = event.getWebServer().getPort();
        String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());

        LogUtils.info("----------- application [{} : {} : {}] 启动完成 -----------", appName, localPort, profile);
    }
}
