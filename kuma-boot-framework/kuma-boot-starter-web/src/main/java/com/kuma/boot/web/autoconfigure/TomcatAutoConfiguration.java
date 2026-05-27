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
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tomcat 自动配置：将 Tomcat 线程模型替换为虚拟线程（Project Loom）
 *
 * @author kuma
 * @since 2021-09-02 21:29:52
 */
@AutoConfiguration(after = TomcatServletWebServerAutoConfiguration.class)
@ConditionalOnClass(TomcatServletWebServerFactory.class)
public class TomcatAutoConfiguration {

    @Bean
    public TomcatServerFactoryCustomizer tomcatServerFactoryCustomizer() {
        LogUtils.started(TomcatAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
        return new TomcatServerFactoryCustomizer();
    }

    public static class TomcatServerFactoryCustomizer
            implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

        @Override
        public void customize(TomcatServletWebServerFactory factory) {
            factory.addProtocolHandlerCustomizers(protocolHandler -> {
                AtomicLong counter = new AtomicLong();
                protocolHandler.setExecutor(
                        Executors.newThreadPerTaskExecutor(
                                Thread.ofVirtual()
                                        .name("kmc-vt-", counter.getAndIncrement())
                                        .factory()
                        )
                );
            });
        }
    }
}
