package com.kuma.boot.mqtt.mqtttemplae.config;

import java.io.Serializable;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class MqttMessageExt implements Serializable {
   private String productId;
   private String deviceId;
   private MqttMessage mqttMessage;

   public MqttMessageExt() {
   }

   public String getProductId() {
      return this.productId;
   }

   public void setProductId(String productId) {
      this.productId = productId;
   }

   public String getDeviceId() {
      return this.deviceId;
   }

   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }

   public MqttMessage getMqttMessage() {
      return this.mqttMessage;
   }

   public void setMqttMessage(MqttMessage mqttMessage) {
      this.mqttMessage = mqttMessage;
   }
}
