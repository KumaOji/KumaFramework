package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class SendFailedException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;

   public SendFailedException(String message) {
      super(DEFAULT_MSG + message);
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "短信发送失败，";
      } else {
         DEFAULT_MSG = "SMS sending failed,";
      }

   }
}
