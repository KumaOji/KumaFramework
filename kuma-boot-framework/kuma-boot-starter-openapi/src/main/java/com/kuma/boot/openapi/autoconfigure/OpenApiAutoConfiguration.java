package com.kuma.boot.openapi.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.openapi.autoconfigure.properties.OpenApiProperties;
import com.kuma.boot.openapi.client.config.ClientBeanScanConfig;
import com.kuma.boot.openapi.server.config.ServerBeanScanConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties({OpenApiProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.openapi",
   name = {"enabled"},
   havingValue = "true"
)
public class OpenApiAutoConfiguration implements InitializingBean {
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(OpenApiAutoConfiguration.class, "kuma-boot-starter-openapi", new String[0]);
   }

   @Import({ClientBeanScanConfig.class})
   @ConditionalOnProperty(
      prefix = "kuma.boot.openapi",
      name = {"client"},
      havingValue = "true"
   )
   public static class OpenApiClientAutoConfiguration {
   }

   @Import({ServerBeanScanConfig.class})
   @ConditionalOnProperty(
      prefix = "kuma.boot.openapi",
      name = {"server"},
      havingValue = "true"
   )
   public static class OpenApiServerAutoConfiguration {
   }
}
