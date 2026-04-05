package com.kuma.boot.openapi.client.config;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@ComponentScans({@ComponentScan({"com.kuma.boot.openapi.client"})})
@EntityScan(
   basePackages = {"com.kuma.boot.openapi.client"}
)
@Configuration
@ConditionalOnProperty(prefix = "kuma.boot.openapi", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ClientBeanScanConfig implements EnvironmentAware {
   public void setEnvironment(Environment environment) {
      LogUtils.info("############################ init kmc-openapi-client BeanScanConfig ##############################", new Object[0]);
   }
}
