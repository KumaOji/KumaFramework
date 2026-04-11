package com.kuma.boot.mail.spring.sender;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mail.spring.event.MailSendEvent;
import com.kuma.boot.mail.spring.model.MailDetails;
import com.kuma.boot.mail.spring.model.MailSendInfo;
import jakarta.mail.MessagingException;
import java.io.File;
import java.time.LocalDateTime;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

public class MailSenderImpl implements MailSender {
   private final JavaMailSender mailSender;
   private final ApplicationEventPublisher eventPublisher;
   private final MailProperties mailProperties;

   public MailSenderImpl(JavaMailSender mailSender, ApplicationEventPublisher eventPublisher, MailProperties mailProperties) {
      this.mailSender = mailSender;
      this.eventPublisher = eventPublisher;
      this.mailProperties = mailProperties;
   }

   public MailSendInfo sendMail(MailDetails mailDetails) {
      MailSendInfo mailSendInfo = new MailSendInfo(mailDetails);
      mailSendInfo.setSentDate(LocalDateTime.now());

      try {
         this.checkMail(mailDetails);
         this.sendMimeMail(mailDetails);
         mailSendInfo.setSuccess(true);
      } catch (Exception e) {
         mailSendInfo.setSuccess(false);
         mailSendInfo.setErrorMsg(e.getMessage());
         LogUtils.error("\u53d1\u9001\u90ae\u4ef6\u5931\u8d25: [{}]", new Object[]{mailDetails, e});
      } finally {
         this.eventPublisher.publishEvent(new MailSendEvent(mailSendInfo));
      }

      return mailSendInfo;
   }

   private void sendMimeMail(MailDetails mailDetails) throws MessagingException {
      MimeMessageHelper messageHelper = new MimeMessageHelper(this.mailSender.createMimeMessage(), true);
      String from = StringUtils.hasText(mailDetails.getFrom()) ? mailDetails.getFrom() : this.mailProperties.getUsername();
      messageHelper.setFrom(from);
      messageHelper.setSubject(mailDetails.getSubject());
      if (mailDetails.getTo() != null && mailDetails.getTo().length > 0) {
         messageHelper.setTo(mailDetails.getTo());
      }

      if (mailDetails.getCc() != null && mailDetails.getCc().length > 0) {
         messageHelper.setCc(mailDetails.getCc());
      }

      if (mailDetails.getBcc() != null && mailDetails.getBcc().length > 0) {
         messageHelper.setBcc(mailDetails.getBcc());
      }

      boolean showHtml = mailDetails.getShowHtml() != null && mailDetails.getShowHtml();
      messageHelper.setText(mailDetails.getContent(), showHtml);
      if (mailDetails.getFiles() != null) {
         for(File file : mailDetails.getFiles()) {
            messageHelper.addAttachment(file.getName(), file);
         }
      }

      this.mailSender.send(messageHelper.getMimeMessage());
      LogUtils.info("\u53d1\u9001\u90ae\u4ef6\u6210\u529f\uff1a[{}]", new Object[]{mailDetails});
   }
}
