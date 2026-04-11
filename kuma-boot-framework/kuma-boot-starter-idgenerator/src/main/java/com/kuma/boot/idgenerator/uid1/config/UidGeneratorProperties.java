package com.kuma.boot.idgenerator.uid1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "uid.gen"
)
public class UidGeneratorProperties {
   private Integer timeBits;
   private Integer workerBits;
   private Integer seqBits;
   private String epochStr;
   private Integer boostPower;
   private Long scheduleInterval;

   public UidGeneratorProperties() {
   }

   public Integer getTimeBits() {
      return this.timeBits;
   }

   public void setTimeBits(Integer timeBits) {
      this.timeBits = timeBits;
   }

   public Integer getWorkerBits() {
      return this.workerBits;
   }

   public void setWorkerBits(Integer workerBits) {
      this.workerBits = workerBits;
   }

   public Integer getSeqBits() {
      return this.seqBits;
   }

   public void setSeqBits(Integer seqBits) {
      this.seqBits = seqBits;
   }

   public String getEpochStr() {
      return this.epochStr;
   }

   public void setEpochStr(String epochStr) {
      this.epochStr = epochStr;
   }

   public Integer getBoostPower() {
      return this.boostPower;
   }

   public void setBoostPower(Integer boostPower) {
      this.boostPower = boostPower;
   }

   public Long getScheduleInterval() {
      return this.scheduleInterval;
   }

   public void setScheduleInterval(Long scheduleInterval) {
      this.scheduleInterval = scheduleInterval;
   }
}
