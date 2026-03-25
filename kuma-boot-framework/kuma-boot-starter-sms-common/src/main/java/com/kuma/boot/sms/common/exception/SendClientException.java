package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class SendClientException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;

   public SendClientException(String message) {
      super(DEFAULT_MSG + message);
   }

   public SendClientException(String message, Throwable cause) {
      super(DEFAULT_MSG + message, cause);
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "短信发送失败，客户端错误，";
      } else {
         DEFAULT_MSG = "SMS sending failed with client exception, ";
      }

   }
}
