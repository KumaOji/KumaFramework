package com.kuma.boot.eventbus.disruptor.tmp4.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "distributor"
)
public class WorkerStationConfig {
   private List<Config> workerStations = new ArrayList();

   public WorkerStationConfig() {
   }

   public List<Config> getWorkerStations() {
      return this.workerStations;
   }

   public void setWorkerStations(List<Config> workerLines) {
      this.workerStations = workerLines;
   }

   public static class Config {
      private String station;
      private int lineSize = 1024;
      private int handlerNum = 1;
      private int keepAliveTime = 60;

      public Config() {
      }

      public String getStation() {
         return this.station;
      }

      public void setStation(String stationName) {
         this.station = stationName;
      }

      public int getLineSize() {
         return this.lineSize;
      }

      public void setLineSize(int lineSize) {
         this.lineSize = lineSize;
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
}
