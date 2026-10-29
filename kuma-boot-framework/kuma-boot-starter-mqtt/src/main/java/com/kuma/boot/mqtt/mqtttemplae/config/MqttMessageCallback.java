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
      LogUtils.info("MQTT关闭连接", new Object[0]);
   }

   public void mqttErrorOccurred(MqttException ex) {
      LogUtils.error("MQTT报错，错误信息：{}，详情见日志", new Object[]{ex.getMessage(), ex});
   }

   public void messageArrived(String topic, MqttMessage message) {
      this.mqttLoadBalancer.messageArrived(topic, message);
   }

   public void deliveryComplete(IMqttToken token) {
      LogUtils.info("MQTT消息发送成功", new Object[0]);
   }

   public void connectComplete(boolean reconnect, String uri) {
      if (!reconnect) {
         LogUtils.info("MQTT建立连接", new Object[0]);
      }

   }

   public void authPacketArrived(int reasonCode, MqttProperties properties) {
      LogUtils.info("接收到身份验证数据包", new Object[0]);
   }
}
