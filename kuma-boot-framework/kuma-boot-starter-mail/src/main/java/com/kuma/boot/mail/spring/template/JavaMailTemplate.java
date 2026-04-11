package com.kuma.boot.mail.spring.template;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ObjectUtils;

public class JavaMailTemplate implements MailTemplate {
   private final JavaMailSender mailSender;
   private final MailProperties mailProperties;

   public JavaMailTemplate(JavaMailSender mailSender, MailProperties mailProperties) {
      this.mailSender = mailSender;
      this.mailProperties = mailProperties;
   }

   public void sendSimpleMail(String to, String subject, String content, String... cc) {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(this.mailProperties.getUsername());
      message.setTo(to);
      message.setSubject(subject);
      message.setText(content);
      if (!ObjectUtils.isEmpty(cc)) {
         message.setCc(cc);
      }

      this.mailSender.send(message);
   }

   public void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException {
      MimeMessage message = this.mailSender.createMimeMessage();
      this.buildHelper(to, subject, content, message, cc);
      this.mailSender.send(message);
   }

   public void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException {
      MimeMessage message = this.mailSender.createMimeMessage();
      MimeMessageHelper helper = this.buildHelper(to, subject, content, message, cc);
      FileSystemResource file = new FileSystemResource(new File(filePath));
      String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
      helper.addAttachment(fileName, file);
      this.mailSender.send(message);
   }

   public void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String... cc) throws MessagingException {
      MimeMessage message = this.mailSender.createMimeMessage();
      MimeMessageHelper helper = this.buildHelper(to, subject, content, message, cc);
      FileSystemResource res = new FileSystemResource(new File(rscPath));
      helper.addInline(rscId, res);
      this.mailSender.send(message);
   }

   private MimeMessageHelper buildHelper(String to, String subject, String content, MimeMessage message, String... cc) throws MessagingException {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(this.mailProperties.getUsername());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(content, true);
      if (!ObjectUtils.isEmpty(cc)) {
         helper.setCc(cc);
      }

      return helper;
   }
}
