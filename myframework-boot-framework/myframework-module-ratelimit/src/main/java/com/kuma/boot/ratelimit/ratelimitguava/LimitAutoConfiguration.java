/*
 *  com.google.common.util.concurrent.RateLimiter
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.ratelimit.ratelimitguava;

import com.google.common.util.concurrent.RateLimiter;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitaspect.LimitProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(value={LimitProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.ratelimit", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class LimitAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(LimitAutoConfiguration.class, (String)"kuma-boot-starter-ratelimit", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnClass(value={RateLimiter.class})
    public GuavaLimitAspect guavaLimitAspect() {
        return new GuavaLimitAspect();
    }
}

