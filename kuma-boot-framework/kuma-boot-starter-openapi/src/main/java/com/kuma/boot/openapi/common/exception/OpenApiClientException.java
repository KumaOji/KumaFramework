package com.kuma.boot.openapi.common.exception;

public class OpenApiClientException extends OpenApiException {
   public OpenApiClientException(String errorMsg) {
      super(errorMsg);
   }

   public OpenApiClientException(String errorMsg, Throwable throwable) {
      super(errorMsg, throwable);
   }
}
