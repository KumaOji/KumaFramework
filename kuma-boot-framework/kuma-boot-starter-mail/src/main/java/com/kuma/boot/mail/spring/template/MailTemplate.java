package com.kuma.boot.mail.spring.template;

import jakarta.mail.MessagingException;

public interface MailTemplate {
   void sendSimpleMail(String to, String subject, String content, String... cc);

   void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException;

   void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException;

   void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String... cc) throws MessagingException;
}
