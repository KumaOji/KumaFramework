package com.kuma.boot.openapi.common.exception;

public class OpenApiException extends RuntimeException {
   public OpenApiException(String errorMsg) {
      super(errorMsg);
   }

   public OpenApiException(String errorMsg, Throwable throwable) {
      super(errorMsg, throwable);
   }
}
