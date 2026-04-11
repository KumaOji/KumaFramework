package com.kuma.boot.websocket.spring.common.exception;

public class ErrorJsonMessageException extends RuntimeException {
   public ErrorJsonMessageException(String message) {
      super(message);
   }
}
