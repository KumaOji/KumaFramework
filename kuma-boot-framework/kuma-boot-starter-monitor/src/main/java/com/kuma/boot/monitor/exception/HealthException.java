package com.kuma.boot.monitor.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class HealthException extends BootException {
   public HealthException() {
   }

   public HealthException(String message) {
      super(message);
   }

   public HealthException(Throwable e) {
      super(e);
   }

   public HealthException(String message, Throwable e) {
      super(message, e);
   }

   public HealthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public HealthException(ResultEnum result) {
      super(result);
   }

   public HealthException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public HealthException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public HealthException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public HealthException(Code code, String message) {
      super(code, message);
   }

   public HealthException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public HealthException(Code code, Throwable e) {
      super(code, e);
   }

   public HealthException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public HealthException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public HealthException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
