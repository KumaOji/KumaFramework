package com.kuma.cloud.gateway.config;

import com.kuma.boot.ip2region.autoconfigure.Ip2regionAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads the Kuma starter features used by this application. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        Ip2regionAutoConfiguration.class
})
public class Ip2regionConfiguration {
}
