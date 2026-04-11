package com.kuma.boot.statemachine.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.statemachine.autoconfigure.properties.StatemachineProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({StatemachineProperties.class})
@AutoConfiguration
public class StatemachineAutoConfiguration implements InitializingBean {
   public StatemachineAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(StatemachineAutoConfiguration.class, "kuma-boot-starter-statemachine", new String[0]);
   }
}
