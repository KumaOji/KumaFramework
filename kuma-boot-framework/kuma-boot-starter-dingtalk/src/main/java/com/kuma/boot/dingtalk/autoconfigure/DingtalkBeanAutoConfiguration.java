/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.dingtalk.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.multi.MultiDingerAlgorithmInjectRegister;
import com.kuma.boot.dingtalk.support.CustomMessage;
import com.kuma.boot.dingtalk.support.DefaultDingerAsyncCallable;
import com.kuma.boot.dingtalk.support.DefaultDingerExceptionCallback;
import com.kuma.boot.dingtalk.support.DefaultDingerIdGenerator;
import com.kuma.boot.dingtalk.support.DingTalkSignAlgorithm;
import com.kuma.boot.dingtalk.support.DingerAsyncCallback;
import com.kuma.boot.dingtalk.support.DingerExceptionCallback;
import com.kuma.boot.dingtalk.support.DingerIdGenerator;
import com.kuma.boot.dingtalk.support.MarkDownMessage;
import com.kuma.boot.dingtalk.support.TextMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix="kuma.boot.dingtalk", value={"enabled"}, havingValue="true")
public class DingtalkBeanAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(DingtalkBeanAutoConfiguration.class, (String)"kuma-boot-starter-dingtalk", (String[])new String[0]);
    }

    @Bean(name={"textMessage"})
    @ConditionalOnMissingBean(name={"textMessage"})
    public CustomMessage textMessage() {
        return new TextMessage();
    }

    @Bean(name={"markDownMessage"})
    @ConditionalOnMissingBean(name={"markDownMessage"})
    public CustomMessage markDownMessage() {
        return new MarkDownMessage();
    }

    @Bean
    public DingTalkSignAlgorithm dingerSignAlgorithm() {
        return new DingTalkSignAlgorithm();
    }

    @Bean
    public DingerIdGenerator dingerIdGenerator() {
        return new DefaultDingerIdGenerator();
    }

    @Bean
    public DingerAsyncCallback dingerAsyncCallback() {
        return new DefaultDingerAsyncCallable();
    }

    @Bean
    public DingerExceptionCallback dingerExceptionCallback() {
        return new DefaultDingerExceptionCallback();
    }

    @Bean
    public MultiDingerAlgorithmInjectRegister multiDingerAlgorithmInjectRegister() {
        return new MultiDingerAlgorithmInjectRegister();
    }
}

