package com.kuma.boot.mqtt.autoconfigure;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;

@Configuration(
   proxyBeanMethods = false
)
public class MqttReceiveHandler {
   private static final Logger log = LoggerFactory.getLogger(MqttReceiveHandler.class);

   public MqttReceiveHandler() {
   }

   @Bean
   public MessageHandler handlerMqtt5Message() {
      return (message) -> {
         MessageHeaders headers = message.getHeaders();
         String receivedTopic = (String)headers.get("mqtt_receivedTopic");
         log.info("获取到v5的消息的topic :{} ", receivedTopic);
         String responseTopic = (String)headers.get("mqtt_responseTopic");
         log.info("获取到v5的消息的响应主题 :{} ", responseTopic);
         String correlationData = null;
         if (Objects.nonNull(headers.get("mqtt_correlationData"))) {
            correlationData = new String((byte[])Objects.requireNonNull(headers.get("mqtt_correlationData")), StandardCharsets.UTF_8);
            log.info("获取到v5的消息关联数据 :{} ", correlationData);
         }

         String payload = new String((byte[])message.getPayload(), StandardCharsets.UTF_8);
         log.info("获取到v5的消息的payload :{} ", payload);
      };
   }
}
