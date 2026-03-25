package com.kuma.boot.oss.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;

public class DuplicateException extends OssException {
   public DuplicateException() {
   }

   public DuplicateException(String message) {
      super(message);
   }

   public DuplicateException(Throwable e) {
      super(e);
   }

   public DuplicateException(String message, Throwable e) {
      super(message, e);
   }

   public DuplicateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public DuplicateException(ResultEnum result) {
      super(result);
   }

   public DuplicateException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public DuplicateException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public DuplicateException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public DuplicateException(Code code, String message) {
      super(code, message);
   }

   public DuplicateException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public DuplicateException(Code code, Throwable e) {
      super(code, e);
   }

   public DuplicateException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public DuplicateException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public DuplicateException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
