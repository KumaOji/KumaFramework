package com.kuma.boot.idgenerator.uid.config;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
   prefix = "kuma.boot.idgenerator.uid"
)
public class UidGenProperties {
   private @NotNull @Length(
   max = 6,
   min = 6
) String systemCode;
   private @NotNull @Length(
   max = 1,
   min = 1
) String dataCenterCode = "A";
   private int timeBits = 26;
   private int workerBits = 24;
   private int seqBits = 13;
   private String epochStr = "2022-04-22";
   private int boostPower = 3;
   private int paddingFactor = 50;
   private Long scheduleInterval;

   public UidGenProperties() {
   }

   public int getTimeBits() {
      return this.timeBits;
   }

   public void setTimeBits(int timeBits) {
      this.timeBits = timeBits;
   }

   public int getWorkerBits() {
      return this.workerBits;
   }

   public void setWorkerBits(int workerBits) {
      this.workerBits = workerBits;
   }

   public int getSeqBits() {
      return this.seqBits;
   }

   public void setSeqBits(int seqBits) {
      this.seqBits = seqBits;
   }

   public String getEpochStr() {
      return this.epochStr;
   }

   public void setEpochStr(String epochStr) {
      this.epochStr = epochStr;
   }

   public int getBoostPower() {
      return this.boostPower;
   }

   public void setBoostPower(int boostPower) {
      this.boostPower = boostPower;
   }

   public int getPaddingFactor() {
      return this.paddingFactor;
   }

   public void setPaddingFactor(int paddingFactor) {
      this.paddingFactor = paddingFactor;
   }

   public Long getScheduleInterval() {
      return this.scheduleInterval;
   }

   public void setScheduleInterval(Long scheduleInterval) {
      this.scheduleInterval = scheduleInterval;
   }

   public String getSystemCode() {
      return this.systemCode;
   }

   public void setSystemCode(String systemCode) {
      this.systemCode = systemCode;
   }

   public String getDataCenterCode() {
      return this.dataCenterCode;
   }

   public void setDataCenterCode(String dataCenterCode) {
      this.dataCenterCode = dataCenterCode;
   }
}
