package com.kuma.boot.mqtt.mqtttemplae.config;

import com.kuma.boot.mqtt.mqtttemplae.template.MqttTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnBean(SpringMqttBrokerProperties.class)
@ConditionalOnProperty(
   prefix = "spring.mqtt-broker",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class MqttAutoConfig {
   public MqttAutoConfig() {
   }

   @Bean(
      name = {"mqttManager"},
      initMethod = "open",
      destroyMethod = "close"
   )
   public MqttManager mqttManager(SpringMqttBrokerProperties springMqttBrokerProperties, MqttLoadBalancer mqttLoadBalancer) {
      return new MqttManager(springMqttBrokerProperties, mqttLoadBalancer);
   }

   @Bean
   public MqttTemplate mqttTemplate(MqttManager mqttManager) {
      return new MqttTemplate(mqttManager);
   }
}
