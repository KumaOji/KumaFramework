package com.kuma.boot.websocket.stomp.core;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class WebSocketException extends BootException {
   public WebSocketException() {
   }

   public WebSocketException(String message) {
      super(message);
   }

   public WebSocketException(Throwable e) {
      super(e);
   }

   public WebSocketException(String message, Throwable e) {
      super(message, e);
   }

   public WebSocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public WebSocketException(ResultEnum result) {
      super(result);
   }

   public WebSocketException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public WebSocketException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public WebSocketException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public WebSocketException(Code code, String message) {
      super(code, message);
   }

   public WebSocketException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public WebSocketException(Code code, Throwable e) {
      super(code, e);
   }

   public WebSocketException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public WebSocketException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public WebSocketException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
