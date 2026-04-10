package com.kuma.boot.pinyin.roses.api.exception;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class PinyinException extends BootException {
   public PinyinException() {
   }

   public PinyinException(String message) {
      super(message);
   }

   public PinyinException(Throwable e) {
      super(e);
   }

   public PinyinException(String message, Throwable e) {
      super(message, e);
   }

   public PinyinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   public PinyinException(ResultEnum result) {
      super(result);
   }

   public PinyinException(StatusEnum status, ResultEnum result) {
      super(status, result);
   }

   public PinyinException(ResultEnum result, Throwable e) {
      super(result, e);
   }

   public PinyinException(StatusEnum status, ResultEnum result, Throwable e) {
      super(status, result, e);
   }

   public PinyinException(Code code, String message) {
      super(code, message);
   }

   public PinyinException(StatusEnum status, Code code, String message) {
      super(status, code, message);
   }

   public PinyinException(Code code, Throwable e) {
      super(code, e);
   }

   public PinyinException(StatusEnum status, Code code, Throwable e) {
      super(status, code, e);
   }

   public PinyinException(Code code, Throwable e, String message) {
      super(code, e, message);
   }

   public PinyinException(StatusEnum status, Code code, Throwable e, String message) {
      super(status, code, e, message);
   }
}
