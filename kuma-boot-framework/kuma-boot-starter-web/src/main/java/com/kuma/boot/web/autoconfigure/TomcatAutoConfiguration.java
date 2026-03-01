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


import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.coyote.ProtocolHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Undertow http2 h2c 配置，对 servlet 开启 自动配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:29:52
 */
@AutoConfiguration
public class TomcatAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(TomcatAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
    }

    /**
     * 实例化UndertowServerFactoryCustomizer，解决undertow启动提示warn的问题
     */
    @Bean
    public TomcatServerFactoryCustomizer tomcatServerFactoryCustomizer() {
        LogUtils.started(TomcatServerFactoryCustomizer.class, StarterNameConstants.WEB_STARTER);
        return new TomcatServerFactoryCustomizer();
    }

    /**
     * 「编程式配置」通过实现WebServerFactoryCustomizer<UndertowServletWebServerFactory>接口来编程式地配置Undertow,可以更加灵活。
     * 解决undertow警告Buffer pool was not set on WebSocketDeploymentInfo
     *
     * @author kuma
     * @version 2021.9
     * @since 2021-09-02 21:30:01
     */
    public static class TomcatServerFactoryCustomizer
            implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

        @Override
        public void customize( TomcatServletWebServerFactory factory ) {

            factory.addProtocolHandlerCustomizers(protocolHandler -> {
                ThreadFactory factory1 = Thread.ofVirtual()
                        .name("kmc_virtual_")
                        //.inheritInheritableThreadLocals(true)
                        .factory();
                protocolHandler.setExecutor(Executors.newThreadPerTaskExecutor(factory1));
            });

            // 2. 配置连接器参数（可选）
//			factory.addConnectorCustomizers(connector -> {
//				// 连接超时
//				connector.setProperty("connectionTimeout", "2000");
//
//				// 最大连接数
//				connector.setProperty("maxConnections", "10000");
//
//				// 对于虚拟线程，传统线程池参数不再重要
//				connector.setProperty("maxThreads", "0"); // 0 表示使用执行器
//				connector.setProperty("minSpareThreads", "0");
//
//				// 启用 Keep-Alive
//				connector.setProperty("keepAliveTimeout", "15000");
//				connector.setProperty("maxKeepAliveRequests", "100");
//
//				log.info("Tomcat connector configured for virtual threads: {}",
//					connector.getProtocol());
//			});

            // 3. 设置其他工厂属性
//			factory.setSessionTimeout(Duration.ofMinutes(30));
//			factory.addContextCustomizers(context -> {
//				// 可以在这里配置 Context
//				context.setSessionTimeout(30);
//			});
        }

    }
}
