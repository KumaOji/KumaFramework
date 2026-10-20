package com.kuma.cloud.uaa.config;

import com.kuma.boot.actuator.endpoint.audit.autoconfigure.AuditEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.druid.autoconfigure.DruidEndpointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.kmc.autoconfigure.KmcEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.request.autoconfigure.RequestMappingEndPointAutoConfiguration;
import com.kuma.boot.actuator.endpoint.startup.autoconfigure.KmcStartupEndpointAutoConfiguration;
import com.kuma.boot.actuator.health.autoconfigure.KmcHealthAutoConfiguration;
import com.kuma.boot.actuator.info.autoconfigure.KmcInfoAutoConfiguration;
import com.kuma.boot.actuator.mbean.autoconfigure.ManagedResourceAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads the Kuma actuator extensions used by the application. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        AuditEndPointAutoConfiguration.class,
        DruidEndpointAutoConfiguration.class,
        KmcEndPointAutoConfiguration.class,
        RequestMappingEndPointAutoConfiguration.class,
        KmcStartupEndpointAutoConfiguration.class,
        KmcHealthAutoConfiguration.class,
        KmcInfoAutoConfiguration.class,
        ManagedResourceAutoConfiguration.class
})
public class ActuatorConfiguration {
}
