package com.kuma.cloud.blog.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        com.kuma.boot.frp.autoconfigure.FrpAutoConfiguration.class
})
public class FrpConfiguration {
}
