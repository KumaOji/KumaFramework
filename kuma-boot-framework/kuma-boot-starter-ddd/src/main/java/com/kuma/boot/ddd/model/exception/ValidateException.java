package com.kuma.boot.ddd.model.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class ValidateException extends BootException {
   public ValidateException() {
   }

   public ValidateException(String message) {
      super(message);
   }

   public ValidateException(Throwable e) {
      super(e);
   }

   public ValidateException(String message, Throwable e) {
      super(message, e);
   }

   public ValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public ValidateException(ResultEnum result) {
      super(result);
   }

   public ValidateException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public ValidateException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public ValidateException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public ValidateException(Code code, String message) {
      super(code, message);
   }

   public ValidateException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public ValidateException(Code code, Throwable e) {
      super(code, e);
   }

   public ValidateException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public ValidateException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public ValidateException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
