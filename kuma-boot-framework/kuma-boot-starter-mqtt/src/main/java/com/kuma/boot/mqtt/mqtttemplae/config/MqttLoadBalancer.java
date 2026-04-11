package com.kuma.boot.mqtt.mqtttemplae.config;

import org.eclipse.paho.mqttv5.common.MqttMessage;

public interface MqttLoadBalancer {
   void messageArrived(String topic, MqttMessage message);
}
