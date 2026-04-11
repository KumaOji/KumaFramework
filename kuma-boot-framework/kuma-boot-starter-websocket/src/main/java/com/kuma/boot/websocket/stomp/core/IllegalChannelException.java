package com.kuma.boot.websocket.stomp.core;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;

public class IllegalChannelException extends WebSocketException {
   public IllegalChannelException() {
   }

   public IllegalChannelException(String message) {
      super(message);
   }

   public IllegalChannelException(Throwable e) {
      super(e);
   }

   public IllegalChannelException(String message, Throwable e) {
      super(message, e);
   }

   public IllegalChannelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public IllegalChannelException(ResultEnum result) {
      super(result);
   }

   public IllegalChannelException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public IllegalChannelException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public IllegalChannelException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public IllegalChannelException(Code code, String message) {
      super(code, message);
   }

   public IllegalChannelException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public IllegalChannelException(Code code, Throwable e) {
      super(code, e);
   }

   public IllegalChannelException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public IllegalChannelException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public IllegalChannelException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
