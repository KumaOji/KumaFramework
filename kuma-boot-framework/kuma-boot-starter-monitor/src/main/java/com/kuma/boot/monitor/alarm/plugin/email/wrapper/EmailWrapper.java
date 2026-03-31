package com.kuma.boot.monitor.alarm.plugin.email.wrapper;

import com.kuma.boot.monitor.alarm.core.loader.entity.RegisterInfo;
import com.kuma.boot.monitor.alarm.core.loader.helper.RegisterInfoLoaderHelper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class EmailWrapper {
   public EmailWrapper() {
   }

   public static HtmlEmail genEmailClient() throws EmailException {
      RegisterInfo info = RegisterInfoLoaderHelper.load();
      HtmlEmail htmlEmail = new HtmlEmail();
      htmlEmail.setCharset("UTF-8");
      htmlEmail.setHostName(info.getEmailHost());
      htmlEmail.setSmtpPort(info.getEmailPort());
      htmlEmail.setFrom(info.getEmailFrom());
      htmlEmail.setAuthentication(info.getEmailUname(), info.getEmailToken());
      if (BooleanUtils.isTrue(info.getEmailSsl())) {
         htmlEmail.setSSLOnConnect(true);
      }

      return htmlEmail;
   }
}
