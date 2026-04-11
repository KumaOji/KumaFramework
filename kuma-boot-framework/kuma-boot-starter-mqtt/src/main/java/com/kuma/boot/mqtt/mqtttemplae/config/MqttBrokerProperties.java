package com.kuma.boot.mqtt.mqtttemplae.config;

import cn.hutool.core.util.IdUtil;
import java.util.HashSet;
import java.util.Set;

public class MqttBrokerProperties {
   private String username = "";
   private String password = "";
   private String uri = "tcp://127.0.0.1:1883";
   private String clientId = String.valueOf(IdUtil.getSnowflakeNextId());
   private int subscribeQos = 0;
   private int sendQos = 1;
   private boolean clearStart = false;
   private int receiveMaximum = 5;
   private long maximumPacketSize = 1024L;
   private int connectionTimeout = 10;
   private int keepAliveInterval = 15;
   private boolean automaticReconnect = true;
   private boolean manualAcks = true;
   private Set<String> topics = new HashSet(0);

   public MqttBrokerProperties() {
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getUri() {
      return this.uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   public String getClientId() {
      return this.clientId;
   }

   public void setClientId(String clientId) {
      this.clientId = clientId;
   }

   public int getSubscribeQos() {
      return this.subscribeQos;
   }

   public void setSubscribeQos(int subscribeQos) {
      this.subscribeQos = subscribeQos;
   }

   public int getSendQos() {
      return this.sendQos;
   }

   public void setSendQos(int sendQos) {
      this.sendQos = sendQos;
   }

   public boolean isClearStart() {
      return this.clearStart;
   }

   public void setClearStart(boolean clearStart) {
      this.clearStart = clearStart;
   }

   public int getReceiveMaximum() {
      return this.receiveMaximum;
   }

   public void setReceiveMaximum(int receiveMaximum) {
      this.receiveMaximum = receiveMaximum;
   }

   public long getMaximumPacketSize() {
      return this.maximumPacketSize;
   }

   public void setMaximumPacketSize(long maximumPacketSize) {
      this.maximumPacketSize = maximumPacketSize;
   }

   public int getConnectionTimeout() {
      return this.connectionTimeout;
   }

   public void setConnectionTimeout(int connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
   }

   public int getKeepAliveInterval() {
      return this.keepAliveInterval;
   }

   public void setKeepAliveInterval(int keepAliveInterval) {
      this.keepAliveInterval = keepAliveInterval;
   }

   public boolean isAutomaticReconnect() {
      return this.automaticReconnect;
   }

   public void setAutomaticReconnect(boolean automaticReconnect) {
      this.automaticReconnect = automaticReconnect;
   }

   public boolean isManualAcks() {
      return this.manualAcks;
   }

   public void setManualAcks(boolean manualAcks) {
      this.manualAcks = manualAcks;
   }

   public Set<String> getTopics() {
      return this.topics;
   }

   public void setTopics(Set<String> topics) {
      this.topics = topics;
   }
}
