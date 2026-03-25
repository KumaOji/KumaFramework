package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class PhoneIsNullException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;

   public PhoneIsNullException() {
      super(DEFAULT_MSG);
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "手机号无效";
      } else {
         DEFAULT_MSG = "Invalid phone number.";
      }

   }
}
