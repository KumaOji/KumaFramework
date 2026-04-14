package com.kuma.boot.data.elasticsearch.autoconfigure;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.elasticsearch.autoconfigure.properties.ElasticsearchProperties;
import com.kuma.boot.data.elasticsearch.autoconfigure.properties.RestClientPoolProperties;
import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({RestClientPoolProperties.class, ElasticsearchProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.data.elasticsearch",
   name = {"enabled"},
   havingValue = "true"
)
@EsMapperScan({"com.kuma.cloud.*.es.mapper"})
public class ElasticsearchAutoConfiguration implements InitializingBean {
   public ElasticsearchAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(ElasticsearchAutoConfiguration.class, "kuma-boot-starter-data-elasticsearch", new String[0]);
   }

   /**
    * Spring Boot 会注册 {@link ElasticsearchTransport} 与 {@link ElasticsearchClient}，通常不注册
    * {@link ElasticsearchAsyncClient}；异步客户端与同步客户端共用同一 transport 即可。
    */
   @Bean
   @ConditionalOnMissingBean(ElasticsearchAsyncClient.class)
   @ConditionalOnBean(ElasticsearchTransport.class)
   public ElasticsearchAsyncClient elasticsearchAsyncClient(ElasticsearchTransport elasticsearchTransport) {
      return new ElasticsearchAsyncClient(elasticsearchTransport);
   }
}
