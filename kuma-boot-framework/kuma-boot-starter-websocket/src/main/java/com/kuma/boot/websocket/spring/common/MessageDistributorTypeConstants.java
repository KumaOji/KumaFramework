package com.kuma.boot.websocket.spring.common;

public final class MessageDistributorTypeConstants {
   public static final String LOCAL = "local";
   public static final String REDIS = "redis";
   public static final String ROCKETMQ = "rocketmq";
   public static final String CUSTOM = "custom";

   private MessageDistributorTypeConstants() {
   }
}
