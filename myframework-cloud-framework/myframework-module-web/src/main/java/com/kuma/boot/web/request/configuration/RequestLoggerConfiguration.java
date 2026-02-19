/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.web.request.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.aspect.RequestLoggerAspect;
import com.kuma.boot.web.request.listener.RequestLoggerListener;
import com.kuma.boot.web.request.properties.RequestLoggerProperties;
import com.kuma.boot.web.request.service.RequestLoggerService;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after={RedisRequestLoggerConfiguration.class, KafkaRequestLoggerConfiguration.class, LoggerRequestLoggerConfiguration.class})
@EnableConfigurationProperties(value={RequestLoggerProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.web.request", name={"enabled"}, havingValue="true")
public class RequestLoggerConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RequestLoggerConfiguration.class, (String)"kuma-boot-starter-logger", (String[])new String[0]);
    }

    @Bean
    public RequestLoggerListener requestLoggerListener(List<RequestLoggerService> requestLoggerServices) {
        return new RequestLoggerListener(requestLoggerServices);
    }

    @Bean
    public RequestLoggerAspect requestLoggerAspect() {
        return new RequestLoggerAspect();
    }
}

