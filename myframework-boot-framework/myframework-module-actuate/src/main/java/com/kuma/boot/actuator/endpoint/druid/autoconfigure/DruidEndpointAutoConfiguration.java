
package com.kuma.boot.actuator.endpoint.druid.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.kuma.boot.actuator.endpoint.druid.DruidEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Druid 端点配置
 */
@AutoConfiguration
@ConditionalOnProperty(
        name = {"spring.datasource.type"},
        havingValue = "com.alibaba.druid.pool.DruidDataSource"
)
@ConditionalOnClass({DruidDataSource.class})
@ConditionalOnAvailableEndpoint(endpoint = DruidEndpoint.class)
public class DruidEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DruidEndpoint druidEndpoint() {
        return new DruidEndpoint();
    }

}
