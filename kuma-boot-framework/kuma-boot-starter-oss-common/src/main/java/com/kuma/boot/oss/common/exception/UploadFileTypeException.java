package com.kuma.boot.oss.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class UploadFileTypeException extends BootException {
   public UploadFileTypeException() {
   }

   public UploadFileTypeException(String message) {
      super(message);
   }

   public UploadFileTypeException(Throwable e) {
      super(e);
   }

   public UploadFileTypeException(String message, Throwable e) {
      super(message, e);
   }

   public UploadFileTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public UploadFileTypeException(ResultEnum result) {
      super(result);
   }

   public UploadFileTypeException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public UploadFileTypeException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public UploadFileTypeException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public UploadFileTypeException(Code code, String message) {
      super(code, message);
   }

   public UploadFileTypeException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public UploadFileTypeException(Code code, Throwable e) {
      super(code, e);
   }

   public UploadFileTypeException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public UploadFileTypeException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public UploadFileTypeException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
