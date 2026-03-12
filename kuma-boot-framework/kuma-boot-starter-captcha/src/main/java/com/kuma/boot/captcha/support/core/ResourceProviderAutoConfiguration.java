//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
@EnableConfigurationProperties({CaptchaProperties.class})
@ConditionalOnProperty(
        prefix = "kuma.boot.captcha.tmp",
        name = {"enabled"},
        havingValue = "true"
)
public class ResourceProviderAutoConfiguration {
    @PostConstruct
    public void postConstruct() {
        LogUtils.info("Starter [Engine Captcha Starter] Auto Configure.", new Object[0]);
    }

    @Bean
    public ResourceProvider resourceProvider(CaptchaProperties captchaProperties) {
        ResourceProvider resourceProvider = new ResourceProvider(captchaProperties);
        LogUtils.info("[Resource Provider] Auto Configure.", new Object[0]);
        return resourceProvider;
    }
}
