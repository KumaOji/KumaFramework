package com.kuma.boot.logger.eden.model;

import java.time.Instant;
import java.util.Objects;

public class AccessLog {
   private String location;
   private String arguments;
   private String returnValue;
   private Throwable throwable;
   private long duration;
   private transient Instant start;
   private transient Instant end;

   public AccessLog() {
   }

   public String toString() {
      String var10000 = this.location;
      return "AccessLog{location='" + var10000 + "', arguments='" + this.arguments + "', returnValue='" + this.returnValue + "', throwable=" + String.valueOf(this.throwable) + ", duration=" + this.duration + ", start=" + String.valueOf(this.start) + ", end=" + String.valueOf(this.end) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         AccessLog accessLog = (AccessLog)o;
         return this.duration == accessLog.duration && Objects.equals(this.location, accessLog.location) && Objects.equals(this.arguments, accessLog.arguments) && Objects.equals(this.returnValue, accessLog.returnValue) && Objects.equals(this.throwable, accessLog.throwable) && Objects.equals(this.start, accessLog.start) && Objects.equals(this.end, accessLog.end);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.location, this.arguments, this.returnValue, this.throwable, this.duration, this.start, this.end});
   }

   public String getLocation() {
      return this.location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getArguments() {
      return this.arguments;
   }

   public void setArguments(String arguments) {
      this.arguments = arguments;
   }

   public String getReturnValue() {
      return this.returnValue;
   }

   public void setReturnValue(String returnValue) {
      this.returnValue = returnValue;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public void setThrowable(Throwable throwable) {
      this.throwable = throwable;
   }

   public long getDuration() {
      return this.duration;
   }

   public void setDuration(long duration) {
      this.duration = duration;
   }

   public Instant getStart() {
      return this.start;
   }

   public void setStart(Instant start) {
      this.start = start;
   }

   public Instant getEnd() {
      return this.end;
   }

   public void setEnd(Instant end) {
      this.end = end;
   }

   public static AccessLogBuilder builder() {
      return new AccessLogBuilder();
   }

   public static final class AccessLogBuilder {
      private String location;
      private String arguments;
      private String returnValue;
      private Throwable throwable;
      private long duration;
      private Instant start;
      private Instant end;

      private AccessLogBuilder() {
      }

      public AccessLogBuilder location(String location) {
         this.location = location;
         return this;
      }

      public AccessLogBuilder arguments(String arguments) {
         this.arguments = arguments;
         return this;
      }

      public AccessLogBuilder returnValue(String returnValue) {
         this.returnValue = returnValue;
         return this;
      }

      public AccessLogBuilder throwable(Throwable throwable) {
         this.throwable = throwable;
         return this;
      }

      public AccessLogBuilder duration(long duration) {
         this.duration = duration;
         return this;
      }

      public AccessLogBuilder start(Instant start) {
         this.start = start;
         return this;
      }

      public AccessLogBuilder end(Instant end) {
         this.end = end;
         return this;
      }

      public AccessLog build() {
         AccessLog accessLog = new AccessLog();
         accessLog.setLocation(this.location);
         accessLog.setArguments(this.arguments);
         accessLog.setReturnValue(this.returnValue);
         accessLog.setThrowable(this.throwable);
         accessLog.setDuration(this.duration);
         accessLog.setStart(this.start);
         accessLog.setEnd(this.end);
         return accessLog;
      }
   }
}
