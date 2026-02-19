/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 */
package com.kuma.boot.web.exception.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.exception.enums.ExceptionHandleTypeEnum;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.exception.handler.LoggerExceptionHandler;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import java.util.Arrays;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@AutoConfiguration
@ConditionalOnProperty(prefix="kuma.boot.web.global-exception", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class LoggerExceptionHandlerAutoConfiguration
implements InitializingBean {
    @Autowired
    private ExceptionHandleProperties exceptionHandleProperties;
    @Autowired
    @Qualifier(value="requestMappingHandlerMapping")
    private RequestMappingHandlerMapping mapping;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(LoggerExceptionHandlerAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    @Bean
    public ExceptionHandler loggerGlobalExceptionHandler() {
        if (Arrays.stream(this.exceptionHandleProperties.getTypes()).anyMatch(e -> e.name().equals(ExceptionHandleTypeEnum.LOGGER.name()))) {
            return new LoggerExceptionHandler(this.mapping);
        }
        return null;
    }
}

