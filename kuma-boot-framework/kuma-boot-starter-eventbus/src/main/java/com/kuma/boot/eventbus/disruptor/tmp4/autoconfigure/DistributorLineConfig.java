package com.kuma.boot.eventbus.disruptor.tmp4.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "distributor.distributor-line"
)
public class DistributorLineConfig {
   private String name = "Distributor";
   private int size = 1024;
   private int handlerNum = 2;
   private int keepAliveTime = 60;

   public DistributorLineConfig() {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getSize() {
      return this.size;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public int getHandlerNum() {
      return this.handlerNum;
   }

   public void setHandlerNum(int handlerNum) {
      this.handlerNum = handlerNum;
   }

   public int getKeepAliveTime() {
      return this.keepAliveTime;
   }

   public void setKeepAliveTime(int keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
   }
}
