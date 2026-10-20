package com.kuma.cloud.uaa.config;

import com.kuma.boot.captcha.autoconfigure.CaptchaAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        CaptchaAutoConfiguration.class
})
public class CaptchaConfiguration {
}
