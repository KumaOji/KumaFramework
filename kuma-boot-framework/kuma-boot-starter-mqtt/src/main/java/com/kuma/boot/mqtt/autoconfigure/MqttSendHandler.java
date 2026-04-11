package com.kuma.boot.mqtt.autoconfigure;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(
   defaultRequestChannel = "mqtt5OutboundChannel"
)
public interface MqttSendHandler {
   void sendToMqtt(String data);

   void sendToMqtt(@Header("mqtt_qos") Integer qos, String data);

   void sendToMqtt(@Header("mqtt_topic") String topic, String data);

   void sendToMqtt(@Header("mqtt_topic") String topic, @Header("mqtt_qos") Integer qos, String data);

   void sendToMqtt(@Header("mqtt_topic") String topic, @Header("mqtt_qos") int qos, byte[] payload);

   void sendToMqtt(@Header("mqtt_topic") String topic, @Header("mqtt_responseTopic") String responseTopic, @Header("mqtt_correlationData") String correlationData, @Header("mqtt_qos") Integer qos, String data);
}
