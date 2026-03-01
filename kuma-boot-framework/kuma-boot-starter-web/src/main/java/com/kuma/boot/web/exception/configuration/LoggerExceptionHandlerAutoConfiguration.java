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

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.exception.enums.ExceptionHandleTypeEnum;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.exception.handler.LoggerExceptionHandler;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;

/**
 * 当web项目引入此依赖时，自动配置对应的内容 初始化log的事件监听与切面配置
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/4/30 10:21
 */
@AutoConfiguration
@ConditionalOnProperty(
        prefix = ExceptionHandleProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class LoggerExceptionHandlerAutoConfiguration implements InitializingBean {

    @Autowired private ExceptionHandleProperties exceptionHandleProperties;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping mapping;

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(
                LoggerExceptionHandlerAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
    }

    @Bean
    public ExceptionHandler loggerGlobalExceptionHandler() {
        if (Arrays.stream(exceptionHandleProperties.getTypes())
                .anyMatch(e -> e.name().equals(ExceptionHandleTypeEnum.LOGGER.name()))) {
            return new LoggerExceptionHandler(mapping);
        }
        return null;
    }
}
