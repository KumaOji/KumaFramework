package com.kuma.boot.mqtt.mqtttemplae.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.paho.mqttv5.common.MqttException;

public class MqttManager {
   private final SpringMqttBrokerProperties springMqttBrokerProperties;
   private final MqttLoadBalancer mqttLoadBalancer;
   private static final Map<String, MqttClient> MQTT_SESSION_MAP = new ConcurrentHashMap(16);

   public MqttManager(SpringMqttBrokerProperties springMqttBrokerProperties, MqttLoadBalancer mqttLoadBalancer) {
      this.springMqttBrokerProperties = springMqttBrokerProperties;
      this.mqttLoadBalancer = mqttLoadBalancer;
   }

   public MqttClient getSession(String key) {
      return (MqttClient)MQTT_SESSION_MAP.get(key);
   }

   public synchronized void open() {
      this.springMqttBrokerProperties.getConfigs().forEach((k, v) -> {
         MqttClient client = new MqttClient(v, this.mqttLoadBalancer);
         client.open();
         MQTT_SESSION_MAP.put(k, client);
      });
   }

   public synchronized void close() {
      this.springMqttBrokerProperties.getConfigs().forEach((k, v) -> {
         MqttClient client = new MqttClient(v, this.mqttLoadBalancer);

         try {
            client.close();
         } catch (MqttException e) {
            throw new RuntimeException(e);
         }

         MQTT_SESSION_MAP.remove(k);
      });
   }
}
