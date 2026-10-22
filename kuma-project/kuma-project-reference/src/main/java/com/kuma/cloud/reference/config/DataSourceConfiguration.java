package com.kuma.cloud.reference.config;

import com.kuma.boot.data.datasource.dynamic.mybatisplus.annotation.EnableDynamicDataSource;
import com.kuma.boot.data.datasource.autoconfigure.TransactionExecutorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.TransactionSupportAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads Kuma datasource routing and transaction support for this project. */
@Configuration(proxyBeanMethods = false)
@EnableDynamicDataSource
@ImportAutoConfiguration({
        TransactionExecutorAutoConfiguration.class,
        TransactionSupportAutoConfiguration.class
})
public class DataSourceConfiguration {
}
