package com.kuma.boot.websocket.stomp.core;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;

public class PrincipalNotFoundException extends WebSocketException {
   public PrincipalNotFoundException() {
   }

   public PrincipalNotFoundException(String message) {
      super(message);
   }

   public PrincipalNotFoundException(Throwable e) {
      super(e);
   }

   public PrincipalNotFoundException(String message, Throwable e) {
      super(message, e);
   }

   public PrincipalNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public PrincipalNotFoundException(ResultEnum result) {
      super(result);
   }

   public PrincipalNotFoundException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public PrincipalNotFoundException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public PrincipalNotFoundException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public PrincipalNotFoundException(Code code, String message) {
      super(code, message);
   }

   public PrincipalNotFoundException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public PrincipalNotFoundException(Code code, Throwable e) {
      super(code, e);
   }

   public PrincipalNotFoundException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public PrincipalNotFoundException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public PrincipalNotFoundException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
