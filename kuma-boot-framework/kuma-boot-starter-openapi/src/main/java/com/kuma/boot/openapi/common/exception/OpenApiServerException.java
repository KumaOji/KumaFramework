package com.kuma.boot.openapi.common.exception;

public class OpenApiServerException extends OpenApiException {
   public OpenApiServerException(String errorMsg) {
      super(errorMsg);
   }

   public OpenApiServerException(String errorMsg, Throwable throwable) {
      super(errorMsg, throwable);
   }
}
