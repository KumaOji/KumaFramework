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
 */
package com.kuma.boot.web.laytpl.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.laytpl.model.FmtFunc;
import com.kuma.boot.web.laytpl.model.LayTplTemplate;
import com.kuma.boot.web.laytpl.properties.LayTplProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(value={LayTplProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.laytpl", name={"enabled"}, havingValue="true")
public class LayTplAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(LayTplAutoConfiguration.class, (String)"kuma-boot-starter-laytpl", (String[])new String[0]);
    }

    @Bean(value={"fmt"})
    public FmtFunc fmtFunc(LayTplProperties properties) {
        return new FmtFunc(properties);
    }

    @Bean(value={"layTpl"})
    public LayTplTemplate layTplTemplate(FmtFunc fmtFunc, LayTplProperties properties) {
        return new LayTplTemplate(properties, fmtFunc);
    }
}

