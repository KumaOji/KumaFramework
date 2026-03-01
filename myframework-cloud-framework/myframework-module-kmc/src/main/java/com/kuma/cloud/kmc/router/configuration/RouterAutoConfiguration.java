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
package com.kuma.cloud.kmc.router.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.kmc.router.properties.RouterProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(value={RouterProperties.class})
@ConditionalOnProperty(prefix="kuma.cloud.kmc.router", name={"enabled"}, havingValue="true")
public class RouterAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RouterAutoConfiguration.class, (String)"kuma-boot-starter-seata", (String[])new String[0]);
    }
}

