package com.kuma.boot.mqtt.mqtttemplae.config;

public interface MqttListener {
   void onMessage(MqttMessageExt messageExt);
}
