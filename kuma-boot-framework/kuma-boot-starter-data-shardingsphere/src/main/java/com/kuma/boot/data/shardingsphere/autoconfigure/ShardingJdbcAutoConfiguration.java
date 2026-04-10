package com.kuma.boot.data.shardingsphere.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.shardingsphere.algorithm.DataSourceShardingAlgorithm;
import com.kuma.boot.data.shardingsphere.autoconfigure.properties.ShardingJdbcProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({ShardingJdbcProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.data.shardingsphere",
   name = {"enabled"},
   havingValue = "true"
)
public class ShardingJdbcAutoConfiguration implements ApplicationContextAware, InitializingBean {
   public ShardingJdbcAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(ShardingJdbcAutoConfiguration.class, "kuma-boot-starter-data-shardingsphere", new String[0]);
   }

   public void setApplicationContext(ApplicationContext context) throws BeansException {
   }

   @Bean
   public DataSourceShardingAlgorithm dataSourceShardingAlgorithm() {
      return new DataSourceShardingAlgorithm();
   }
}
