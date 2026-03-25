package com.kuma.boot.oss.common.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class UploadFileException extends BootException {
   public UploadFileException() {
   }

   public UploadFileException(String message) {
      super(message);
   }

   public UploadFileException(Throwable e) {
      super(e);
   }

   public UploadFileException(String message, Throwable e) {
      super(message, e);
   }

   public UploadFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public UploadFileException(ResultEnum result) {
      super(result);
   }

   public UploadFileException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public UploadFileException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public UploadFileException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public UploadFileException(Code code, String message) {
      super(code, message);
   }

   public UploadFileException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public UploadFileException(Code code, Throwable e) {
      super(code, e);
   }

   public UploadFileException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public UploadFileException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public UploadFileException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
