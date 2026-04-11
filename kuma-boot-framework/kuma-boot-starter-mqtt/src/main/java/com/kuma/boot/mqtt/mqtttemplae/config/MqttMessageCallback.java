package com.kuma.boot.mqtt.mqtttemplae.config;

import com.kuma.boot.common.utils.log.LogUtils;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttMessageCallback implements MqttCallback {
   private final MqttLoadBalancer mqttLoadBalancer;
   private final MqttBrokerProperties mqttBrokerProperties;
   private final org.eclipse.paho.mqttv5.client.MqttClient client;

   public MqttMessageCallback(MqttLoadBalancer mqttLoadBalancer, MqttBrokerProperties mqttBrokerProperties, org.eclipse.paho.mqttv5.client.MqttClient client) {
      this.mqttLoadBalancer = mqttLoadBalancer;
      this.mqttBrokerProperties = mqttBrokerProperties;
      this.client = client;
   }

   public void disconnected(MqttDisconnectResponse disconnectResponse) {
      LogUtils.info("MQTT\u5173\u95ed\u8fde\u63a5", new Object[0]);
   }

   public void mqttErrorOccurred(MqttException ex) {
      LogUtils.error("MQTT\u62a5\u9519\uff0c\u9519\u8bef\u4fe1\u606f\uff1a{}\uff0c\u8be6\u60c5\u89c1\u65e5\u5fd7", new Object[]{ex.getMessage(), ex});
   }

   public void messageArrived(String topic, MqttMessage message) {
      this.mqttLoadBalancer.messageArrived(topic, message);
   }

   public void deliveryComplete(IMqttToken token) {
      LogUtils.info("MQTT\u6d88\u606f\u53d1\u9001\u6210\u529f", new Object[0]);
   }

   public void connectComplete(boolean reconnect, String uri) {
      if (!reconnect) {
         LogUtils.info("MQTT\u5efa\u7acb\u8fde\u63a5", new Object[0]);
      }

   }

   public void authPacketArrived(int reasonCode, MqttProperties properties) {
      LogUtils.info("\u63a5\u6536\u5230\u8eab\u4efd\u9a8c\u8bc1\u6570\u636e\u5305", new Object[0]);
   }
}
