package com.kuma.cloud.reference.config;

import com.kuma.boot.data.datasource.autoconfigure.DataSourceProcessorAutoConfiguration;
import com.kuma.boot.data.datasource.autoconfigure.DataSourceRoutingAutoConfiguration;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceExecutor;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceInvoker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DataSourceStarterConfigurationTest {
    @Test
    void routingSupportBacksOffWithoutADataSource() {
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(
                DataSourceProcessorAutoConfiguration.class, DataSourceRoutingAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(DataSourceExecutor.class);
                    assertThat(context).doesNotHaveBean(DataSourceInvoker.class);
                });
    }
}
