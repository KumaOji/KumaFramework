package com.kuma.boot.eventbus.atlas.exception;

public class EventException extends RuntimeException {
   public EventException(String message) {
      super(message);
   }

   public EventException(String message, Throwable cause) {
      super(message, cause);
   }
}
