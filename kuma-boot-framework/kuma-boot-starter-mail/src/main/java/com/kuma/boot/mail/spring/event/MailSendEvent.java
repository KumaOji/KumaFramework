package com.kuma.boot.mail.spring.event;

import com.kuma.boot.mail.spring.model.MailSendInfo;
import org.springframework.context.ApplicationEvent;

public class MailSendEvent extends ApplicationEvent {
   public MailSendEvent(MailSendInfo mailSendInfo) {
      super(mailSendInfo);
   }

   public String toString() {
      return super.toString();
   }
}
