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

package com.kuma.boot.web.exception.configuration;

import com.kuma.boot.web.exception.advice.BusinessHandlerExceptionAdvice;
import com.kuma.boot.web.exception.advice.InnerApiExceptionAdvice;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * 异常自动配置
 *
 * @author kuma
 * @version 2023.08
 * @since 2023-08-02 17:43:32
 */
@AutoConfiguration(
        after = {
                com.kuma.boot.web.exception.configuration.DingTalkExceptionHandlerAutoConfiguration.class,
                LoggerExceptionHandlerAutoConfiguration.class
        })
@ConditionalOnProperty(
        prefix = ExceptionHandleProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties(ExceptionHandleProperties.class)
public class ExceptionAutoConfiguration {

    @Bean
    public BusinessHandlerExceptionAdvice businessHandlerExceptionAdvice(
            List<ExceptionHandler> exceptionHandlers) {
        return new BusinessHandlerExceptionAdvice(exceptionHandlers);
    }

    @Bean
    public InnerApiExceptionAdvice innerApiExceptionAdvice(
            List<ExceptionHandler> exceptionHandlers,
            ApplicationEventPublisher applicationEventPublisher,
            @Qualifier("asyncThreadPoolTaskExecutor")
            ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor) {
        return new InnerApiExceptionAdvice(
                exceptionHandlers, asyncThreadPoolTaskExecutor, applicationEventPublisher);
    }
}
