package com.kuma.boot.metrics.autoconfigure;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.metrics.autoconfigure.properties.DruidMetricsProperties;
import com.kuma.boot.metrics.autoconfigure.properties.MetricsProperties;
import com.kuma.boot.metrics.autoconfigure.properties.SentinelMetricsProperties;
import com.kuma.boot.metrics.autoconfigure.properties.UndertowMetricsProperties;
import com.kuma.boot.metrics.druid.DruidDataSourcePoolMetadata;
import com.kuma.boot.metrics.druid.DruidMetrics;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceUnwrapper;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@AutoConfiguration
@EnableConfigurationProperties({MetricsProperties.class, DruidMetricsProperties.class, SentinelMetricsProperties.class, UndertowMetricsProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.metrics",
   name = {"enabled"},
   havingValue = "true"
)
public class MetricsAutoConfiguration implements InitializingBean {
   public MetricsAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(MetricsAutoConfiguration.class, "kuma-boot-starter-metrics", new String[0]);
   }

   @Configuration
   @ConditionalOnClass({DruidDataSource.class})
   @ConditionalOnProperty(
      prefix = "kuma.boot.metrics.druid",
      name = {"enabled"},
      havingValue = "true"
   )
   public static class DruidMetricsConfiguration {
      private static final String DATASOURCE_SUFFIX = "dataSource";

      public DruidMetricsConfiguration() {
      }

      @Bean
      public DataSourcePoolMetadataProvider druidDataSourceMetadataProvider() {
         return (dataSource) -> {
            DruidDataSource druidDataSource = (DruidDataSource)DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class);
            return druidDataSource != null ? new DruidDataSourcePoolMetadata(druidDataSource) : null;
         };
      }

      @Bean
      @ConditionalOnMissingBean
      public StatFilter statFilter() {
         return new StatFilter();
      }

      @Bean
      public DruidMetrics druidMetrics(ObjectProvider<Map<String, DataSource>> dataSourcesProvider) {
         Map<String, DataSource> dataSourceMap = (Map)dataSourcesProvider.getIfAvailable(HashMap::new);
         Map<String, DruidDataSource> druidDataSourceMap = new HashMap(2);
         dataSourceMap.forEach((name, dataSource) -> druidDataSourceMap.put(getDataSourceName(name), (DruidDataSource)DataSourceUnwrapper.unwrap(dataSource, DruidDataSource.class)));
         return new DruidMetrics(druidDataSourceMap);
      }

      private static String getDataSourceName(String beanName) {
         return beanName.length() > "dataSource".length() && StringUtils.endsWithIgnoreCase(beanName, "dataSource") ? beanName.substring(0, beanName.length() - "dataSource".length()) : beanName;
      }
   }

   @Configuration
   @ConditionalOnProperty(
      prefix = "kuma.boot.metrics.sentinel",
      name = {"enabled"},
      havingValue = "true"
   )
   public static class SentinelMetricsConfiguration {
      public SentinelMetricsConfiguration() {
      }
   }
}
