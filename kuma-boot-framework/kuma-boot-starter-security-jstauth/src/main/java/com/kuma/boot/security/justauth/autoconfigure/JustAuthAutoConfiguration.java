//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.security.justauth.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.justauth.autoconfigure.properties.JustAuthProperties;
import com.kuma.boot.security.justauth.factory.AuthRequestFactory;
import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "kuma.boot.security.justauth",
        name = {"enabled"},
        havingValue = "true"
)
@EnableConfigurationProperties({JustAuthProperties.class})
public class JustAuthAutoConfiguration implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(JustAuthAutoConfiguration.class, "kuma-boot-starter-security-justauth", new String[0]);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "justauth",
            value = {"enabled"},
            havingValue = "true",
            matchIfMissing = true
    )
    public AuthRequestFactory authRequestFactory(JustAuthProperties properties, AuthStateCache authStateCache) {
        return new AuthRequestFactory(properties, authStateCache);
    }

    @Configuration
    @Import({JustAuthStateCacheConfiguration.Default.class, JustAuthStateCacheConfiguration.Redis.class, JustAuthStateCacheConfiguration.Custom.class})
    protected static class AuthStateCacheAutoConfiguration {
    }
}
