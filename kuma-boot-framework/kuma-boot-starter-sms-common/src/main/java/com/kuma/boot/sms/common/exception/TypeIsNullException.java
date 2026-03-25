package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class TypeIsNullException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;

   public TypeIsNullException() {
      super(DEFAULT_MSG);
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "类型无效";
      } else {
         DEFAULT_MSG = "type is null";
      }

   }
}
