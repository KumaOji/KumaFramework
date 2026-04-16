package com.kuma.boot.mqtt.mqtttemplae.config;

import cn.hutool.core.collection.CollUtil;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "kuma.boot.mqtt", name = "enabled", havingValue = "true")
@ConfigurationProperties(
   prefix = "kuma.boot.mqtt.spring.broker"
)
public class SpringMqttBrokerProperties implements InitializingBean {
   private Boolean enabled = false;
   private Map<String, MqttBrokerProperties> configs = Map.of("default", new MqttBrokerProperties());

   public SpringMqttBrokerProperties() {
   }

   public void afterPropertiesSet() {
      if (!Boolean.TRUE.equals(this.enabled)) {
         return;
      }
      this.configs.forEach((k, v) -> {
         if (CollUtil.isEmpty(v.getTopics())) {
            throw new IllegalStateException("Topics must not be empty.");
         }
      });
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public Map<String, MqttBrokerProperties> getConfigs() {
      return this.configs;
   }

   public void setConfigs(Map<String, MqttBrokerProperties> configs) {
      this.configs = configs;
   }
}
