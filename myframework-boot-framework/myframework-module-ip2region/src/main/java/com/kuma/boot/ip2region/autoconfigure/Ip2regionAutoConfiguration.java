/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.ImportRuntimeHints
 *  org.springframework.core.io.ResourceLoader
 */
package com.kuma.boot.ip2region.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ip2region.autoconfigure.properties.Ip2regionProperties;
import com.kuma.boot.ip2region.impl.Ip2regionSearcherImpl;
import com.kuma.boot.ip2region.model.Ip2regionSearcher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ResourceLoader;

@AutoConfiguration
@ImportRuntimeHints(value={Ip2regionRuntimeHintsRegistrar.class})
@EnableConfigurationProperties(value={Ip2regionProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.ip2region", name={"enabled"}, havingValue="true")
public class Ip2regionAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(Ip2regionAutoConfiguration.class, (String)"kuma-boot-starter-ip2region", (String[])new String[0]);
    }

    @Bean
    public Ip2regionSearcher ip2regionSearcher(ResourceLoader resourceLoader, Ip2regionProperties properties) {
        return new Ip2regionSearcherImpl(resourceLoader, properties);
    }
}

