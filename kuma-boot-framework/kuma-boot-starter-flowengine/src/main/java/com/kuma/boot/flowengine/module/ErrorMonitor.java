package com.kuma.boot.flowengine.module;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ErrorMonitor {
   private @NotNull String errorMonitorClass;
   private @Valid ExceptionMapping exceptionMapping = new ExceptionMapping();

   public ErrorMonitor() {
   }

   public String getErrorMonitorClass() {
      return this.errorMonitorClass;
   }

   public void setErrorMonitorClass(String errorMonitorClass) {
      this.errorMonitorClass = errorMonitorClass;
   }

   public ExceptionMapping getExceptionMapping() {
      return this.exceptionMapping;
   }

   public void setExceptionMapping(ExceptionMapping exceptionMapping) {
      this.exceptionMapping = exceptionMapping;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("ErrorMonitor{");
      sb.append("errorMonitorClass='").append(this.errorMonitorClass).append('\'');
      sb.append(",exceptionMapping=").append(this.exceptionMapping);
      sb.append('}');
      return sb.toString();
   }
}
