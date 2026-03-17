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

import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.BootContextUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.server.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * WebServletEventListenerAutoConfiguration
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@AutoConfiguration
public class WebServletEventListenerAutoConfiguration {

    @Configuration
    public static class ServletWebServerInitializedEventListener
            implements ApplicationListener<ServletWebServerInitializedEvent> {

        @Override
        public void onApplicationEvent( ServletWebServerInitializedEvent event ) {

            LogUtils.info("Application [{}] WebServerInitialized StartupDate: {}  port: {}",
                    BootContextUtils.getApplicationName(event.getApplicationContext()),
                    DateUtils.formatTimestamp(event.getApplicationContext().getStartupDate()),
                    event.getWebServer().getPort());

            //LogUtils.info(
            //        "ServletWebServerInitializedEventListener ServletWebServerInitializedEvent onApplicationEvent {}",
            //        event);
        }
    }

    @Configuration
    public static class ServletRequestHandledEventListener implements ApplicationListener<ServletRequestHandledEvent> {

        @Override
        public void onApplicationEvent( ServletRequestHandledEvent event ) {
            // 返回请求的URL。
            String url = event.getRequestUrl();
            // 返回请求来源的IP地址。
            String client = event.getClientAddress();
            // 以毫秒为单位返回请求的处理时间。
            long time = event.getProcessingTimeMillis();
            // 返回请求的HTTP方法(通常是GET或POST)。
            String method = event.getMethod();
            // 返回响应的HTTP状态码
            int statusCode = event.getStatusCode();

            LogUtils.info("客户端IP: {}, 请求的URL: {}, HTTP方法: {}, HTTP状态码: {}, 请求处理时长: {}ms", client, url,
                    method, statusCode, time);
        }
    }
}
