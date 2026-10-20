package com.kuma.cloud.uaa.config;

import com.kuma.boot.idempotent.autoconfigure.IdempotentAutoConfiguration;
import com.kuma.boot.idempotent.idempotetduplicate.PreventDuplicateSubmitAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        IdempotentAutoConfiguration.class,
        PreventDuplicateSubmitAutoConfiguration.class
})
public class IdempotentConfiguration {
}
