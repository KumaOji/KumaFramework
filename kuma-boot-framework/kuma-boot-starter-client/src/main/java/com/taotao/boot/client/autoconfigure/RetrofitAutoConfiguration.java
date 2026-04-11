package com.taotao.boot.client.autoconfigure;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitScan;
import com.kuma.boot.client.properties.RetrofitProperties;
import com.kuma.boot.common.support.property.YamlPropertySourceFactory;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@EnableConfigurationProperties({RetrofitProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.third-client.retrofit",
   name = {"enabled"},
   havingValue = "true"
)
@RetrofitScan(
   basePackages = {"com.kuma.boot.client"}
)
@PropertySource(
   factory = YamlPropertySourceFactory.class,
   value = {"classpath:retrofit.yml"}
)
public class RetrofitAutoConfiguration implements InitializingBean {
   public RetrofitAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(RetrofitAutoConfiguration.class, "kuma-boot-starter-third-client", new String[0]);
   }
}
