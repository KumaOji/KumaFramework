package com.kuma.boot.canal.autoconfigure;

import com.kuma.boot.canal.autoconfigure.properties.CanalProperties;
import com.kuma.boot.canal.client.CanalClient;
import com.kuma.boot.canal.client.SimpleCanalClient;
import com.kuma.boot.canal.runner.CanalApplicationRunner;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({CanalProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.canal",
   name = {"enabled"},
   havingValue = "true"
)
public class CanalAutoConfiguration implements InitializingBean {
   public CanalAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(CanalAutoConfiguration.class, "kuma-boot-starter-canal", new String[0]);
   }

   @Bean
   public CanalClient canalClient(CanalProperties properties) {
      return new SimpleCanalClient(properties);
   }

   @Bean
   public CanalApplicationRunner canalApplicationRunner(CanalClient canalClient) {
      return new CanalApplicationRunner(canalClient);
   }
}
