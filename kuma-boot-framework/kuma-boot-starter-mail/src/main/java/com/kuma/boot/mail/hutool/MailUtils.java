package com.kuma.boot.mail.hutool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MailUtils {
   private final MailAccount ACCOUNT;

   public MailUtils(MailAccount account) {
      this.ACCOUNT = account;
   }

   public MailAccount getMailAccount() {
      return this.ACCOUNT;
   }

   public MailAccount getMailAccount(String from, String user, String pass) {
      this.ACCOUNT.setFrom((String)ObjectUtil.defaultIfBlank(from, this.ACCOUNT.getFrom()));
      this.ACCOUNT.setUser((String)ObjectUtil.defaultIfBlank(user, this.ACCOUNT.getUser()));
      this.ACCOUNT.setPass((String)ObjectUtil.defaultIfBlank(pass, String.valueOf(this.ACCOUNT.getPass())));
      return this.ACCOUNT;
   }

   public String sendText(String to, String subject, String content, File... files) {
      return this.send(to, subject, content, false, files);
   }

   public String sendHtml(String to, String subject, String content, File... files) {
      return this.send(to, subject, content, true, files);
   }

   public String send(String to, String subject, String content, boolean isHtml, File... files) {
      return this.send((Collection)this.splitAddress(to), subject, content, isHtml, files);
   }

   public String send(String to, String cc, String bcc, String subject, String content, boolean isHtml, File... files) {
      return this.send((Collection)this.splitAddress(to), (Collection)this.splitAddress(cc), (Collection)this.splitAddress(bcc), subject, content, isHtml, files);
   }

   public String sendText(Collection<String> tos, String subject, String content, File... files) {
      return this.send(tos, subject, content, false, files);
   }

   public String sendHtml(Collection<String> tos, String subject, String content, File... files) {
      return this.send(tos, subject, content, true, files);
   }

   public String send(Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
      return this.send((Collection)tos, (Collection)null, (Collection)null, subject, content, isHtml, files);
   }

   public String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
      return this.send(this.getMailAccount(), true, tos, ccs, bccs, subject, content, (Map)null, isHtml, files);
   }

   public String send(MailAccount mailAccount, String to, String subject, String content, boolean isHtml, File... files) {
      return this.send((MailAccount)mailAccount, (Collection)this.splitAddress(to), subject, content, isHtml, files);
   }

   public String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
      return this.send((MailAccount)mailAccount, tos, (Collection)null, (Collection)null, subject, content, isHtml, files);
   }

   public String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
      return this.send(mailAccount, false, tos, ccs, bccs, subject, content, (Map)null, isHtml, files);
   }

   public String sendHtml(String to, String subject, String content, Map<String, InputStream> imageMap, File... files) {
      return this.send(to, subject, content, imageMap, true, files);
   }

   public String send(String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return this.send((Collection)this.splitAddress(to), subject, content, (Map)imageMap, isHtml, files);
   }

   public String send(String to, String cc, String bcc, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return this.send((Collection)this.splitAddress(to), this.splitAddress(cc), this.splitAddress(bcc), subject, content, (Map)imageMap, isHtml, files);
   }

   public String sendHtml(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, File... files) {
      return this.send(tos, subject, content, imageMap, true, files);
   }

   public String send(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return this.send((Collection)tos, (Collection)null, (Collection)null, subject, content, (Map)imageMap, isHtml, files);
   }

   public String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return this.send(this.getMailAccount(), true, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
   }

   public String send(MailAccount mailAccount, String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return this.send((MailAccount)mailAccount, (Collection)this.splitAddress(to), subject, content, (Map)imageMap, isHtml, files);
   }

   public String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return this.send(mailAccount, tos, (Collection)null, (Collection)null, subject, content, imageMap, isHtml, files);
   }

   public String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return this.send(mailAccount, false, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
   }

   public Session getSession(final MailAccount mailAccount, boolean isSingleton) {
      Authenticator authenticator = null;
      if (mailAccount.isAuth()) {
         authenticator = new Authenticator() {
            {
               Objects.requireNonNull(MailUtils.this);
            }

            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(mailAccount.getUser(), String.valueOf(mailAccount.getPass()));
            }
         };
      }

      return isSingleton ? Session.getDefaultInstance(mailAccount.getSmtpProps(), authenticator) : Session.getInstance(mailAccount.getSmtpProps(), authenticator);
   }

   private String send(MailAccount mailAccount, boolean useGlobalSession, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      Mail mail = Mail.create(mailAccount).setUseGlobalSession(useGlobalSession);
      if (CollUtil.isNotEmpty(ccs)) {
         mail.setCcs((String[])ccs.toArray(new String[0]));
      }

      if (CollUtil.isNotEmpty(bccs)) {
         mail.setBccs((String[])bccs.toArray(new String[0]));
      }

      mail.setTos((String[])tos.toArray(new String[0]));
      mail.setTitle(subject);
      mail.setContent(content);
      mail.setHtml(isHtml);
      mail.setFiles(files);
      if (MapUtil.isNotEmpty(imageMap)) {
         for(Map.Entry<String, InputStream> entry : imageMap.entrySet()) {
            mail.addImage((String)entry.getKey(), (InputStream)entry.getValue());
            IoUtil.close((Closeable)entry.getValue());
         }
      }

      return mail.send();
   }

   private List<String> splitAddress(String addresses) {
      if (StrUtil.isBlank(addresses)) {
         return null;
      } else {
         List<String> result;
         if (StrUtil.contains(addresses, ',')) {
            result = CharSequenceUtil.splitTrim(addresses, ",");
         } else if (StrUtil.contains(addresses, ';')) {
            result = CharSequenceUtil.splitTrim(addresses, ";");
         } else {
            result = ListUtil.of(new String[]{addresses});
         }

         return result;
      }
   }
}
