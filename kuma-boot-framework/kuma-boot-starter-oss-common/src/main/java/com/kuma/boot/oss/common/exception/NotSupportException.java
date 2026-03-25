package com.kuma.boot.oss.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;

public class NotSupportException extends OssException {
   public NotSupportException() {
   }

   public NotSupportException(String message) {
      super(message);
   }

   public NotSupportException(Throwable e) {
      super(e);
   }

   public NotSupportException(String message, Throwable e) {
      super(message, e);
   }

   public NotSupportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public NotSupportException(ResultEnum result) {
      super(result);
   }

   public NotSupportException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public NotSupportException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public NotSupportException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public NotSupportException(Code code, String message) {
      super(code, message);
   }

   public NotSupportException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public NotSupportException(Code code, Throwable e) {
      super(code, e);
   }

   public NotSupportException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public NotSupportException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public NotSupportException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
