package com.kuma.boot.data.elasticsearch.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.elasticsearch.autoconfigure.properties.ElasticsearchProperties;
import com.kuma.boot.data.elasticsearch.autoconfigure.properties.RestClientPoolProperties;
import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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
}
