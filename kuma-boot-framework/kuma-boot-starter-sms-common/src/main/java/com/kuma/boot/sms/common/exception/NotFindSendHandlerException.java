package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class NotFindSendHandlerException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;

   public NotFindSendHandlerException() {
      super(DEFAULT_MSG);
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "未找到有效的短信发送处理程序";
      } else {
         DEFAULT_MSG = "Not found effective sms send handler.";
      }

   }
}
