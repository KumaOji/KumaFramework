package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class VerificationCodeIsNullException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;

   public VerificationCodeIsNullException() {
      super(DEFAULT_MSG);
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "手机验证码信息无效";
      } else {
         DEFAULT_MSG = "The mobile verification code information is invalid.";
      }

   }
}
