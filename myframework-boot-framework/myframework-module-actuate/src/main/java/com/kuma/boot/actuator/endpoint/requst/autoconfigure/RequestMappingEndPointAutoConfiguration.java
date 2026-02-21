/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.actuator.endpoint.requst.autoconfigure;

import com.kuma.boot.actuator.endpoint.requst.RequestMappingEndPoint;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint=RequestMappingEndPoint.class)
public class RequestMappingEndPointAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(RequestMappingEndPointAutoConfiguration.class, (String)"kuma-boot-starter-actuator", (String[])new String[0]);
    }

    @Bean
    public RequestMappingEndPoint requestMappingEndPoint() {
        return new RequestMappingEndPoint();
    }
}

