/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 */
package com.kuma.cloud.kmc.ratelimit.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.kmc.ratelimit.properties.RatelimitProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(value={RatelimitProperties.class})
@ConditionalOnProperty(prefix="kuma.cloud.kmc.ratelimit", name={"enabled"}, havingValue="true")
public class RatelimitAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RatelimitAutoConfiguration.class, (String)"kuma-boot-starter-seata", (String[])new String[0]);
    }
}

