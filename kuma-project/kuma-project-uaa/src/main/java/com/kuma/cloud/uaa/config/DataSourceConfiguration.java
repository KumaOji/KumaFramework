package com.kuma.cloud.uaa.config;

import com.kuma.boot.data.datasource.autoconfigure.DataSourceRoutingAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.DataSourceProcessorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionExecutorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionSupportAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads Kuma datasource routing and transaction support for this project. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        DynamicDataSourceAutoConfiguration.class,
        DataSourceProcessorAutoConfiguration.class,
        DataSourceRoutingAutoConfiguration.class,
        TransactionExecutorAutoConfiguration.class,
        TransactionSupportAutoConfiguration.class
})
public class DataSourceConfiguration {
}
