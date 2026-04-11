package com.kuma.boot.mqtt.mqtttemplae.template;

import com.kuma.boot.mqtt.mqtttemplae.config.MqttManager;
import org.eclipse.paho.mqttv5.common.MqttException;

public class MqttTemplate {
   private final MqttManager mqttManager;

   public MqttTemplate(MqttManager mqttManager) {
      this.mqttManager = mqttManager;
   }

   public void send(String key, String topic, String payload, int qos) throws MqttException {
      this.mqttManager.getSession(key).send(topic, payload, qos);
   }
}
