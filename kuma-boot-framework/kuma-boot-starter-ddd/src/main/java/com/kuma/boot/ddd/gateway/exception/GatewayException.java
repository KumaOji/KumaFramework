package com.kuma.boot.ddd.gateway.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.Code;

public class GatewayException extends BusinessException {
   public GatewayException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }

   public GatewayException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public GatewayException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public GatewayException(Code code, Throwable e) {
      super(code, e);
   }

   public GatewayException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public GatewayException(Code code, String message) {
      super(code, message);
   }

   public GatewayException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public GatewayException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public GatewayException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public GatewayException(ResultEnum result) {
      super(result);
   }

   public GatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public GatewayException(String message, Throwable e) {
      super(message, e);
   }

   public GatewayException(Throwable e) {
      super(e);
   }

   public GatewayException(String message) {
      super(message);
   }

   public GatewayException() {
   }
}
