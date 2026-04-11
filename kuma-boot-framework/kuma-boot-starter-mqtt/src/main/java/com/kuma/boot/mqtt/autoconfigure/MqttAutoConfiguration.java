package com.kuma.boot.mqtt.autoconfigure;

import com.alibaba.fastjson2.JSONB;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mqtt.autoconfigure.properties.MqttProperties;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;

@Configuration(
   proxyBeanMethods = false
)
@EnableConfigurationProperties({MqttProperties.class})
public class MqttAutoConfiguration implements InitializingBean {
   private static final Logger log = LoggerFactory.getLogger(MqttAutoConfiguration.class);

   public MqttAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(MqttAutoConfiguration.class, "kuma-boot-starter-mqtt", new String[0]);
   }

   @PostConstruct
   public void postConstruct() {
      log.debug("[kmc] |- SDK [Message Mqtt] Auto Configure.");
   }

   @Bean
   public ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager(MqttProperties mqttProperties) {
      MqttConnectionOptions mqttConnectionOptions = new MqttConnectionOptions();
      mqttConnectionOptions.setUserName(mqttProperties.getUsername());
      mqttConnectionOptions.setPassword(JSONB.toBytes(mqttProperties.getPassword(), StandardCharsets.UTF_8));
      mqttConnectionOptions.setCleanStart(mqttProperties.getCleanStart());
      mqttConnectionOptions.setKeepAliveInterval(this.toInt(mqttProperties.getKeepAliveInterval()));
      mqttConnectionOptions.setAutomaticReconnect(mqttProperties.getAutomaticReconnect());
      mqttConnectionOptions.setAutomaticReconnectDelay(this.toInt(mqttProperties.getAutomaticReconnectMinDelay()), this.toInt(mqttProperties.getAutomaticReconnectMaxDelay()));
      log.info("[kmc] |- Bean [Mqtt Connection Options] Auto Configure.");
      Mqttv5ClientManager clientManager = new Mqttv5ClientManager(mqttConnectionOptions, mqttProperties.getClientId());
      clientManager.setPersistence(new MqttDefaultFilePersistence());
      return clientManager;
   }

   @Bean
   @ServiceActivator(
      inputChannel = "mqtt5OutboundChannel"
   )
   public IntegrationFlow mqtt5InFlowTopic2(ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager) {
      Mqttv5PahoMessageDrivenChannelAdapter messageProducer = new Mqttv5PahoMessageDrivenChannelAdapter(clientManager, new String[]{"topic2"});
      return ((IntegrationFlowBuilder)IntegrationFlow.from(messageProducer).channel((c) -> c.queue("fromMqttChannel"))).get();
   }

   @Bean
   public IntegrationFlow mqttOutFlow(ClientManager<IMqttAsyncClient, MqttConnectionOptions> clientManager) {
      return (f) -> f.handle(new Mqttv5PahoMessageHandler(clientManager));
   }

   private int toInt(Duration duration) {
      long value = duration.getSeconds();
      return Long.valueOf(value).intValue();
   }
}
