package com.kuma.boot.mqtt.autoconfigure.properties;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.mqtt"
)
public class MqttProperties {
   private List<String> serverUrls;
   private String username;
   private String password;
   private String clientId = "herodotus-client-manager-client-id-v5";
   private Duration keepAliveInterval = Duration.ofSeconds(60L);
   private Boolean automaticReconnect = true;
   private Duration automaticReconnectMinDelay = Duration.ofSeconds(1L);
   private Duration automaticReconnectMaxDelay = Duration.ofSeconds(120L);
   private Boolean cleanStart = true;

   public MqttProperties() {
   }

   public List<String> getServerUrls() {
      return this.serverUrls;
   }

   public void setServerUrls(List<String> serverUrls) {
      this.serverUrls = serverUrls;
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

   public String getClientId() {
      return this.clientId;
   }

   public void setClientId(String clientId) {
      this.clientId = clientId;
   }

   public Duration getKeepAliveInterval() {
      return this.keepAliveInterval;
   }

   public void setKeepAliveInterval(Duration keepAliveInterval) {
      this.keepAliveInterval = keepAliveInterval;
   }

   public Boolean getAutomaticReconnect() {
      return this.automaticReconnect;
   }

   public void setAutomaticReconnect(Boolean automaticReconnect) {
      this.automaticReconnect = automaticReconnect;
   }

   public Duration getAutomaticReconnectMinDelay() {
      return this.automaticReconnectMinDelay;
   }

   public void setAutomaticReconnectMinDelay(Duration automaticReconnectMinDelay) {
      this.automaticReconnectMinDelay = automaticReconnectMinDelay;
   }

   public Duration getAutomaticReconnectMaxDelay() {
      return this.automaticReconnectMaxDelay;
   }

   public void setAutomaticReconnectMaxDelay(Duration automaticReconnectMaxDelay) {
      this.automaticReconnectMaxDelay = automaticReconnectMaxDelay;
   }

   public Boolean getCleanStart() {
      return this.cleanStart;
   }

   public void setCleanStart(Boolean cleanStart) {
      this.cleanStart = cleanStart;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("username", this.username).add("password", this.password).add("clientId", this.clientId).add("keepAliveInterval", this.keepAliveInterval).add("automaticReconnect", this.automaticReconnect).add("automaticReconnectMinDelay", this.automaticReconnectMinDelay).add("automaticReconnectMaxDelay", this.automaticReconnectMaxDelay).add("cleanStart", this.cleanStart).toString();
   }
}
