package com.kuma.cloud.uaa.config;

import com.kuma.boot.totp.autoconfigure.TotpAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads the Kuma starter features used by this application. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        TotpAutoConfiguration.class
})
public class TotpConfiguration {
}
