package com.kuma.boot.logger.eden.access.config;

import java.util.Objects;

public class AccessLogConfig {
   private boolean enabledMdc = true;
   private String expression;
   private double sampleRate = (double)1.0F;
   private boolean logArguments = true;
   private boolean logReturnValue = true;
   private boolean logExecutionTime = true;
   private int maxLength = 500;
   private long slowThreshold = 1000L;

   public AccessLogConfig() {
   }

   public String toString() {
      return "AccessLogConfig{enabledMdc=" + this.enabledMdc + ", expression='" + this.expression + "', sampleRate=" + this.sampleRate + ", logArguments=" + this.logArguments + ", logReturnValue=" + this.logReturnValue + ", logExecutionTime=" + this.logExecutionTime + ", maxLength=" + this.maxLength + ", slowThreshold=" + this.slowThreshold + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         AccessLogConfig that = (AccessLogConfig)o;
         return this.enabledMdc == that.enabledMdc && Double.compare(this.sampleRate, that.sampleRate) == 0 && this.logArguments == that.logArguments && this.logReturnValue == that.logReturnValue && this.logExecutionTime == that.logExecutionTime && this.maxLength == that.maxLength && this.slowThreshold == that.slowThreshold && Objects.equals(this.expression, that.expression);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.enabledMdc, this.expression, this.sampleRate, this.logArguments, this.logReturnValue, this.logExecutionTime, this.maxLength, this.slowThreshold});
   }

   public boolean isEnabledMdc() {
      return this.enabledMdc;
   }

   public void setEnabledMdc(boolean enabledMdc) {
      this.enabledMdc = enabledMdc;
   }

   public String getExpression() {
      return this.expression;
   }

   public void setExpression(String expression) {
      this.expression = expression;
   }

   public double getSampleRate() {
      return this.sampleRate;
   }

   public void setSampleRate(double sampleRate) {
      this.sampleRate = sampleRate;
   }

   public boolean isLogArguments() {
      return this.logArguments;
   }

   public void setLogArguments(boolean logArguments) {
      this.logArguments = logArguments;
   }

   public boolean isLogReturnValue() {
      return this.logReturnValue;
   }

   public void setLogReturnValue(boolean logReturnValue) {
      this.logReturnValue = logReturnValue;
   }

   public boolean isLogExecutionTime() {
      return this.logExecutionTime;
   }

   public void setLogExecutionTime(boolean logExecutionTime) {
      this.logExecutionTime = logExecutionTime;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void setMaxLength(int maxLength) {
      this.maxLength = maxLength;
   }

   public long getSlowThreshold() {
      return this.slowThreshold;
   }

   public void setSlowThreshold(long slowThreshold) {
      this.slowThreshold = slowThreshold;
   }
}
