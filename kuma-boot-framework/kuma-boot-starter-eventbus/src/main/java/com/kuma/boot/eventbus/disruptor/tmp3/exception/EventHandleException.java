package com.kuma.boot.eventbus.disruptor.tmp3.exception;

public class EventHandleException extends RuntimeException {
   public EventHandleException(Exception e) {
      super(e.getMessage(), (Throwable)null);
   }

   public EventHandleException(String errorMessage) {
      super(errorMessage, (Throwable)null);
   }

   public EventHandleException(String errorMessage, Throwable cause) {
      super(errorMessage, cause);
   }
}
