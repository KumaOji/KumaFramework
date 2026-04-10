package com.kuma.boot.job.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class JobCommonException extends BootException {
   public JobCommonException() {
   }

   public JobCommonException(String message) {
      super(message);
   }

   public JobCommonException(Throwable e) {
      super(e);
   }

   public JobCommonException(String message, Throwable e) {
      super(message, e);
   }

   public JobCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public JobCommonException(ResultEnum result) {
      super(result);
   }

   public JobCommonException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public JobCommonException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public JobCommonException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public JobCommonException(Code code, String message) {
      super(code, message);
   }

   public JobCommonException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public JobCommonException(Code code, Throwable e) {
      super(code, e);
   }

   public JobCommonException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public JobCommonException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public JobCommonException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
