/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.springdoc.knife4j.spring.configuration.insight;

import com.kuma.boot.springdoc.knife4j.spring.insight.Knife4jInsightDiscoveryBootstrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value={Knife4jInsightProperties.class})
@ConditionalOnProperty(name={"knife4j.insight.enable"}, havingValue="true")
public class Knife4jInsightAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Knife4jInsightDiscoveryBootstrapper knife4jInsightDiscoveryBootstrapper(Knife4jInsightProperties insightProperties) {
        return new Knife4jInsightDiscoveryBootstrapper(insightProperties);
    }
}

