/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.annotation.Bean
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 */
package com.kuma.boot.web.exception.configuration;

import com.kuma.boot.web.exception.advice.BusinessHandlerExceptionAdvice;
import com.kuma.boot.web.exception.advice.InnerApiExceptionAdvice;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@AutoConfiguration(after={DingTalkExceptionHandlerAutoConfiguration.class, LoggerExceptionHandlerAutoConfiguration.class, MailExceptionHandlerAutoConfiguration.class})
@ConditionalOnProperty(prefix="kuma.boot.web.global-exception", name={"enabled"}, havingValue="true", matchIfMissing=true)
@EnableConfigurationProperties(value={ExceptionHandleProperties.class})
public class ExceptionAutoConfiguration {
    @Bean
    public BusinessHandlerExceptionAdvice businessHandlerExceptionAdvice(List<ExceptionHandler> exceptionHandlers) {
        return new BusinessHandlerExceptionAdvice(exceptionHandlers);
    }

    @Bean
    public InnerApiExceptionAdvice innerApiExceptionAdvice(List<ExceptionHandler> exceptionHandlers, ApplicationEventPublisher applicationEventPublisher, @Qualifier(value="asyncThreadPoolTaskExecutor") ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor) {
        return new InnerApiExceptionAdvice(exceptionHandlers, asyncThreadPoolTaskExecutor, applicationEventPublisher);
    }
}

