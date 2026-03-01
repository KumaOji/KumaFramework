/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.annotation.PostConstruct
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.captcha.support.core;

import com.kuma.boot.captcha.support.core.properties.CaptchaProperties;
import com.kuma.boot.captcha.support.core.provider.ResourceProvider;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(value={CaptchaProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.captcha.tmp", name={"enabled"}, havingValue="true")
public class ResourceProviderAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        LogUtils.info((String)"Starter [Engine Captcha Starter] Auto Configure.", (Object[])new Object[0]);
    }

    @Bean
    public ResourceProvider resourceProvider(CaptchaProperties captchaProperties) {
        ResourceProvider resourceProvider = new ResourceProvider(captchaProperties);
        LogUtils.info((String)"[Resource Provider] Auto Configure.", (Object[])new Object[0]);
        return resourceProvider;
    }
}

