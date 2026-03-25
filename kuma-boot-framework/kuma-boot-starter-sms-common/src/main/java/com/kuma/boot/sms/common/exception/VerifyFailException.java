package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class VerifyFailException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;

   public VerifyFailException() {
      super(DEFAULT_MSG);
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "验证失败";
      } else {
         DEFAULT_MSG = "Validation fails";
      }

   }
}
