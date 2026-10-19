package com.kuma.boot.data.datasource.autoconfigure;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceExecutor;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceInvoker;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/** 数据源路由与配置来源解耦，使用 YAML/Nacos 时无需读取数据库配置表。 */
@AutoConfiguration(afterName = "com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration")
@ConditionalOnClass(DynamicRoutingDataSource.class)
@ConditionalOnBean(DataSource.class)
public class DataSourceRoutingAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DataSourceInvoker dataSourceInvoker() {
        return new DataSourceInvoker();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceExecutor dataSourceExecutor(DataSource dataSource, DataSourceInvoker invoker) {
        return new DataSourceExecutor(dataSource, invoker);
    }
}
