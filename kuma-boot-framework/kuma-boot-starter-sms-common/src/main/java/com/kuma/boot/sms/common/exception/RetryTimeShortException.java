package com.kuma.boot.sms.common.exception;

import java.util.Locale;

public class RetryTimeShortException extends SmsException {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_MSG;
   private final long surplus;

   public RetryTimeShortException(long surplus) {
      super(String.format(DEFAULT_MSG, surplus));
      this.surplus = surplus;
   }

   public long getSurplus() {
      return this.surplus;
   }

   static {
      Locale locale = Locale.getDefault();
      if (Locale.CHINA.equals(locale)) {
         DEFAULT_MSG = "重试时间过短，请等待%d秒后重试";
      } else {
         DEFAULT_MSG = "Retry time is short, please wait %d second and try again.";
      }

   }
}
