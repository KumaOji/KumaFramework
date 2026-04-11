package com.kuma.boot.websocket.spring.autoconfigure.properties;

import com.kuma.boot.websocket.spring.common.handler.ConcurrentWebSocketSessionOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("kuma.boot.websocket.spring")
public class WebSocketSpringProperties {
   public static final String PREFIX = "kuma.boot.websocket.spring";
   private Boolean enabled = true;
   private String path = "/ws";
   private String[] allowedOrigins;
   private String[] allowedOriginPatterns = new String[]{"*"};
   private boolean supportPartialMessages = false;
   private boolean heartbeat = true;
   private boolean mapSession = true;
   private boolean withSockjs = false;
   @NestedConfigurationProperty
   private ConcurrentWebSocketSessionOptions concurrent = new ConcurrentWebSocketSessionOptions();
   private MessageDistributorTypeEnum messageDistributor;

   public WebSocketSpringProperties() {
      this.messageDistributor = WebSocketSpringProperties.MessageDistributorTypeEnum.LOCAL;
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String[] getAllowedOrigins() {
      return this.allowedOrigins;
   }

   public void setAllowedOrigins(String[] allowedOrigins) {
      this.allowedOrigins = allowedOrigins;
   }

   public String[] getAllowedOriginPatterns() {
      return this.allowedOriginPatterns;
   }

   public void setAllowedOriginPatterns(String[] allowedOriginPatterns) {
      this.allowedOriginPatterns = allowedOriginPatterns;
   }

   public boolean isSupportPartialMessages() {
      return this.supportPartialMessages;
   }

   public void setSupportPartialMessages(boolean supportPartialMessages) {
      this.supportPartialMessages = supportPartialMessages;
   }

   public boolean isHeartbeat() {
      return this.heartbeat;
   }

   public void setHeartbeat(boolean heartbeat) {
      this.heartbeat = heartbeat;
   }

   public boolean isMapSession() {
      return this.mapSession;
   }

   public void setMapSession(boolean mapSession) {
      this.mapSession = mapSession;
   }

   public boolean isWithSockjs() {
      return this.withSockjs;
   }

   public void setWithSockjs(boolean withSockjs) {
      this.withSockjs = withSockjs;
   }

   public ConcurrentWebSocketSessionOptions getConcurrent() {
      return this.concurrent;
   }

   public void setConcurrent(ConcurrentWebSocketSessionOptions concurrent) {
      this.concurrent = concurrent;
   }

   public MessageDistributorTypeEnum getMessageDistributor() {
      return this.messageDistributor;
   }

   public void setMessageDistributor(MessageDistributorTypeEnum messageDistributor) {
      this.messageDistributor = messageDistributor;
   }

   static enum MessageDistributorTypeEnum {
      LOCAL,
      REDIS,
      CUSTOM;

      private MessageDistributorTypeEnum() {
      }

      // $FF: synthetic method
      private static MessageDistributorTypeEnum[] $values() {
         return new MessageDistributorTypeEnum[]{LOCAL, REDIS, CUSTOM};
      }
   }
}
