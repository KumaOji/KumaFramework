/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer
 *  org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory
 *  org.springframework.boot.web.server.WebServerFactoryCustomizer
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class TomcatAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(TomcatAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    @Bean
    public TomcatServerFactoryCustomizer tomcatServerFactoryCustomizer() {
        LogUtils.started(TomcatServerFactoryCustomizer.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
        return new TomcatServerFactoryCustomizer();
    }

    public static class TomcatServerFactoryCustomizer
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        public void customize(TomcatServletWebServerFactory factory) {
            factory.addProtocolHandlerCustomizers(new TomcatProtocolHandlerCustomizer[]{protocolHandler -> {
                ThreadFactory factory1 = Thread.ofVirtual().name("kmc_virtual_").factory();
                protocolHandler.setExecutor((Executor)Executors.newThreadPerTaskExecutor(factory1));
            }});
        }
    }
}

