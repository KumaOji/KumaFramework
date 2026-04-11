package com.kuma.boot.flowengine.simpleflow.api.exception;

public class ValidationException extends FlowException {
   private static final long serialVersionUID = 1L;

   public ValidationException(String message) {
      super(message);
   }

   public ValidationException(String message, Throwable cause) {
      super(message, cause);
   }

   public ValidationException(Throwable cause) {
      super(cause.getMessage(), cause);
   }
}
