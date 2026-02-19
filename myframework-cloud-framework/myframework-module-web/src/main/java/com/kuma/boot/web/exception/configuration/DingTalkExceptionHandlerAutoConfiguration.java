/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.dingtalk.model.DingerSender
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.web.exception.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.model.DingerSender;
import com.kuma.boot.web.exception.enums.ExceptionHandleTypeEnum;
import com.kuma.boot.web.exception.handler.DingTalkExceptionHandler;
import com.kuma.boot.web.exception.handler.ExceptionHandler;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import java.util.Arrays;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(value={DingerSender.class})
@ConditionalOnBean(value={DingerSender.class})
@ConditionalOnProperty(prefix="kuma.boot.web.global-exception", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class DingTalkExceptionHandlerAutoConfiguration
implements InitializingBean {
    @Autowired
    private ExceptionHandleProperties exceptionHandleProperties;
    @Value(value="${spring.application.name: unknown-application}")
    private String applicationName;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingTalkExceptionHandlerAutoConfiguration.class, (String)"kuma-boot-starter-dingtalk", (String[])new String[0]);
    }

    @Bean
    public ExceptionHandler dingTalkGlobalExceptionHandler(DingerSender dingerSender) {
        if (Arrays.stream(this.exceptionHandleProperties.getTypes()).anyMatch(e -> e.name().equals(ExceptionHandleTypeEnum.DING_TALK.name()))) {
            return new DingTalkExceptionHandler(this.exceptionHandleProperties, dingerSender, this.applicationName);
        }
        return null;
    }
}

