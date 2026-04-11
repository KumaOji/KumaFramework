package com.kuma.boot.mqtt.mqtttemplae.config;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.nio.charset.StandardCharsets;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttClient {
   private static final String WILL_TOPIC = "will_topic";
   private static final byte[] WILL_DATA;
   private final MqttBrokerProperties mqttBrokerProperties;
   private org.eclipse.paho.mqttv5.client.MqttClient client;
   private final MqttLoadBalancer mqttLoadBalancer;

   public MqttClient(MqttBrokerProperties mqttBrokerProperties, MqttLoadBalancer mqttLoadBalancer) {
      this.mqttBrokerProperties = mqttBrokerProperties;
      this.mqttLoadBalancer = mqttLoadBalancer;
   }

   public void open() {
      try {
         this.client = new org.eclipse.paho.mqttv5.client.MqttClient(this.mqttBrokerProperties.getUri(), this.mqttBrokerProperties.getClientId(), new MemoryPersistence());
         this.client.setManualAcks(this.mqttBrokerProperties.isManualAcks());
         this.client.setCallback(new MqttMessageCallback(this.mqttLoadBalancer, this.mqttBrokerProperties, this.client));
         this.client.connect(this.options());
         this.client.subscribe((String[])this.mqttBrokerProperties.getTopics().toArray((x$0) -> new String[x$0]), this.mqttBrokerProperties.getTopics().stream().mapToInt((item) -> this.mqttBrokerProperties.getSubscribeQos()).toArray());
         LogUtils.info("MQTT\u8fde\u63a5\u6210\u529f", new Object[0]);
      } catch (Exception e) {
         LogUtils.error("MQTT\u8fde\u63a5\u5931\u8d25\uff0c\u9519\u8bef\u4fe1\u606f\uff1a{}", new Object[]{e.getMessage(), e});
      }

   }

   public void close() throws MqttException {
      if (ObjectUtils.isNotNull(this.client)) {
         this.client.disconnectForcibly(10L);
         this.client.close();
         LogUtils.info("\u5173\u95edMQTT\u8fde\u63a5", new Object[0]);
      }

   }

   public void send(String topic, String payload, int qos) throws MqttException {
      this.client.publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos, false);
   }

   public void send(String topic, String payload) throws MqttException {
      this.send(topic, payload, this.mqttBrokerProperties.getSendQos());
   }

   private MqttConnectionOptions options() {
      MqttConnectionOptions options = new MqttConnectionOptions();
      options.setCleanStart(this.mqttBrokerProperties.isClearStart());
      options.setUserName(this.mqttBrokerProperties.getUsername());
      options.setPassword(this.mqttBrokerProperties.getPassword().getBytes(StandardCharsets.UTF_8));
      options.setReceiveMaximum(this.mqttBrokerProperties.getReceiveMaximum());
      options.setMaximumPacketSize(this.mqttBrokerProperties.getMaximumPacketSize());
      options.setWill("will_topic", new MqttMessage(WILL_DATA, this.mqttBrokerProperties.getSendQos(), false, new MqttProperties()));
      options.setConnectionTimeout(this.mqttBrokerProperties.getConnectionTimeout());
      options.setKeepAliveInterval(this.mqttBrokerProperties.getKeepAliveInterval());
      options.setAutomaticReconnect(this.mqttBrokerProperties.isAutomaticReconnect());
      return options;
   }

   static {
      WILL_DATA = "offline".getBytes(StandardCharsets.UTF_8);
   }
}
