package com.kuma.cloud.lab.config;

import com.kuma.boot.data.datasource.autoconfigure.TransactionExecutorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionSupportAutoConfiguration;
import com.kuma.boot.data.vector.autoconfigure.VectorStoreAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads Kuma starter configurations used by the lab application. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        TransactionExecutorAutoConfiguration.class,
        TransactionSupportAutoConfiguration.class,
        VectorStoreAutoConfiguration.class
})
public class KumaStarterConfiguration {
}
