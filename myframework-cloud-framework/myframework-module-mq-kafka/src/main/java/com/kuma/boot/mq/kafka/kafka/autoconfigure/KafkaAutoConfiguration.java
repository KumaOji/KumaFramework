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
package com.kuma.boot.mq.kafka.kafka.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mq.kafka.kafka.autoconfigure.properties.KafkaProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(value={KafkaProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.mq.kafka", name={"enabled"}, havingValue="true")
public class KafkaAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(KafkaAutoConfiguration.class, (String)"kuma-boot-starter-mq-kafka", (String[])new String[0]);
    }
}

