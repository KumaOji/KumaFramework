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

package com.kuma.boot.web.autoconfigure;

import cn.hutool.http.HttpUtil;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.runtime.listener.ApplicationStartedListener;
import com.kuma.boot.web.controller.RequestController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Undertow http2 h2c 配置，对 servlet 开启 自动配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:29:52
 */
@AutoConfiguration
@Import(RequestController.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(ServletWebServerApplicationContext.class)
// @Profile({"!test"})
public class PostOpenApiAutoConfiguration extends ApplicationStartedListener
        implements InitializingBean {

    // 在测试环境中使用这个类注入会有问题
    @Autowired
    private ServletWebServerApplicationContext servletWebServerApplicationContext;
    @Autowired
    private ServerProperties serverProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(PostOpenApiAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
    }

    @Override
    protected void eventCallback(ApplicationStartedEvent event) {
        int port = servletWebServerApplicationContext.getWebServer().getPort();
//        int port = serverProperties.getPort();

        new Thread(new PostOpenApiRunnable("127.0.0.1", port)).start();
    }

    public static class PostOpenApiRunnable implements Runnable {

        private final String ip;
        private final int port;

        public PostOpenApiRunnable(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            LogUtils.info("开始进行Healthcheck");

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                LogUtils.error(e);
                return;
            }

            try {
                HttpUtil.get("http://" + ip + ":" + port + "/v3/api-docs", 5000);

            } catch (Exception ignored) {
                LogUtils.info("进行Healthcheck失败");
            }

            LogUtils.info("进行Healthcheck成功");
        }
    }
}
