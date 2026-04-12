package com.kuma.boot.client.autoconfigure;

import com.dtflys.forest.springboot.annotation.ForestScan;
import com.kuma.boot.client.properties.ForestProperties;
import com.kuma.boot.common.support.property.YamlPropertySourceFactory;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@EnableConfigurationProperties({ForestProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.third-client.forest",
   name = {"enabled"},
   havingValue = "true"
)
@ForestScan(
   basePackages = {"com.kuma.boot.client"}
)
@PropertySource(
   factory = YamlPropertySourceFactory.class,
   value = {"classpath:forest.yml"}
)
public class ForestAutoConfiguration implements InitializingBean {
   public ForestAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(ForestAutoConfiguration.class, "kuma-boot-starter-third-client", new String[0]);
   }
}
