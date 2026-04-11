package com.kuma.boot.mail.spring.sender;

import com.kuma.boot.mail.spring.model.MailDetails;
import com.kuma.boot.mail.spring.model.MailSendInfo;
import java.util.List;
import org.springframework.mail.MailSendException;
import org.springframework.util.StringUtils;

public interface MailSender {
   MailSendInfo sendMail(MailDetails mailDetails);

   default MailSendInfo sendMail(String subject, String content, boolean showHtml, String... to) {
      MailDetails mailDetails = new MailDetails();
      mailDetails.setShowHtml(showHtml);
      mailDetails.setSubject(subject);
      mailDetails.setContent(content);
      mailDetails.setTo(to);
      return this.sendMail(mailDetails);
   }

   default MailSendInfo sendTextMail(String subject, String content, String... to) {
      return this.sendMail(subject, content, false, to);
   }

   default MailSendInfo sendTextMail(String subject, String content, List<String> to) {
      return this.sendMail(subject, content, false, (String[])to.toArray(new String[0]));
   }

   default MailSendInfo sendHtmlMail(String subject, String content, String... to) {
      return this.sendMail(subject, content, true, to);
   }

   default MailSendInfo sendHtmlMail(String subject, String content, List<String> to) {
      return this.sendHtmlMail(subject, content, (String[])to.toArray(new String[0]));
   }

   default void checkMail(MailDetails mailDetails) {
      boolean noTo = mailDetails.getTo() == null || mailDetails.getTo().length <= 0;
      boolean noCc = mailDetails.getCc() == null || mailDetails.getCc().length <= 0;
      boolean noBcc = mailDetails.getBcc() == null || mailDetails.getBcc().length <= 0;
      if (noTo && noCc && noBcc) {
         throw new MailSendException("The email should have at least one recipient");
      } else if (!StringUtils.hasText(mailDetails.getSubject())) {
         throw new MailSendException("The subject of the email cannot be empty");
      } else if (!StringUtils.hasText(mailDetails.getContent())) {
         throw new MailSendException("The content of the email cannot be empty");
      }
   }
}
