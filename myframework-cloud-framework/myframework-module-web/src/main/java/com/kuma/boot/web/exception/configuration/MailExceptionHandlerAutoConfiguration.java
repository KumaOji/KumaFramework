/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.mail.autoconfigure.MailProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.mail.javamail.JavaMailSender
 */
package com.kuma.boot.web.exception.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.exception.enums.ExceptionHandleTypeEnum;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.exception.handler.MailExceptionHandler;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import java.util.Arrays;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@AutoConfiguration
@ConditionalOnClass(value={JavaMailSender.class})
@ConditionalOnBean(value={JavaMailSender.class, MailProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.web.global-exception", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class MailExceptionHandlerAutoConfiguration
implements InitializingBean {
    private final ExceptionHandleProperties exceptionHandleProperties;
    private final MailProperties mailProperties;
    @Value(value="${spring.application.name: unknown-application}")
    private String applicationName;

    public MailExceptionHandlerAutoConfiguration(ExceptionHandleProperties exceptionHandleProperties, MailProperties mailProperties) {
        this.exceptionHandleProperties = exceptionHandleProperties;
        this.mailProperties = mailProperties;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(MailExceptionHandlerAutoConfiguration.class, (String)"kuma-boot-starter-logger", (String[])new String[0]);
    }

    @Bean
    public ExceptionHandler mailGlobalExceptionHandler(JavaMailSender mailSender) {
        if (Arrays.stream(this.exceptionHandleProperties.getTypes()).anyMatch(e -> e.name().equals(ExceptionHandleTypeEnum.MAIL.name()))) {
            return new MailExceptionHandler(this.mailProperties, this.exceptionHandleProperties, mailSender, this.applicationName);
        }
        return null;
    }
}

