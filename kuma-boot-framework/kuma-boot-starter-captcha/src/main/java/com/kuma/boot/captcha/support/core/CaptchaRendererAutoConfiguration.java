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

import com.kuma.boot.captcha.support.behavior.configuration.BehaviorCaptchaConfiguration;
import com.kuma.boot.captcha.support.core.processor.CaptchaRendererFactory;
import com.kuma.boot.captcha.support.core.properties.CaptchaProperties;
import com.kuma.boot.captcha.support.graphic.configuration.GraphicCaptchaConfiguration;
import com.kuma.boot.captcha.support.hutool.configuration.HutoolCaptchaConfiguration;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after={BehaviorCaptchaConfiguration.class, GraphicCaptchaConfiguration.class, HutoolCaptchaConfiguration.class})
@EnableConfigurationProperties(value={CaptchaProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.captcha.tmp", name={"enabled"}, havingValue="true")
public class CaptchaRendererAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        LogUtils.info((String)"Starter [Engine Captcha Starter] Auto Configure.", (Object[])new Object[0]);
    }

    @Bean
    public CaptchaRendererFactory captchaRendererFactory() {
        CaptchaRendererFactory captchaRendererFactory = new CaptchaRendererFactory();
        LogUtils.info((String)"Bean [Captcha Renderer Factory] Auto Configure.", (Object[])new Object[0]);
        return captchaRendererFactory;
    }
}

