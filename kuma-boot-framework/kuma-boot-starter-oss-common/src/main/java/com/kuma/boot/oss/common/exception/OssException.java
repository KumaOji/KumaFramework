package com.kuma.boot.oss.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class OssException extends BootException {
   public OssException() {
   }

   public OssException(String message) {
      super(message);
   }

   public OssException(Throwable e) {
      super(e);
   }

   public OssException(String message, Throwable e) {
      super(message, e);
   }

   public OssException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public OssException(ResultEnum result) {
      super(result);
   }

   public OssException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public OssException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public OssException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public OssException(Code code, String message) {
      super(code, message);
   }

   public OssException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public OssException(Code code, Throwable e) {
      super(code, e);
   }

   public OssException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public OssException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public OssException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
