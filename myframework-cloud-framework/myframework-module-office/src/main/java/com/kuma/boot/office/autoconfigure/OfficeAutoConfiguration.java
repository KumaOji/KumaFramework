/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 */
package com.kuma.boot.office.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.autoconfigure.properties.OfficeProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(value={OfficeProperties.class})
@AutoConfiguration
public class OfficeAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(OfficeAutoConfiguration.class, (String)"kuma-boot-starter-office", (String[])new String[0]);
    }
}

