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
package com.kuma.cloud.alibaba.nacos.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.alibaba.nacos.properties.NacosCloudProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(value={NacosCloudProperties.class})
@ConditionalOnProperty(prefix="kuma.cloud.alibaba.nacos", name={"enabled"}, havingValue="true")
public class NacosCloudAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(NacosCloudAutoConfiguration.class, (String)"kuma-boot-starter-seata", (String[])new String[0]);
    }
}

