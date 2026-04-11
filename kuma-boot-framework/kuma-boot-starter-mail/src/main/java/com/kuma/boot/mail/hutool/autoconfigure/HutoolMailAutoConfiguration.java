package com.kuma.boot.mail.hutool.autoconfigure;

import cn.hutool.extra.mail.MailAccount;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mail.hutool.MailUtils;
import com.kuma.boot.mail.hutool.autoconfigure.properties.MailProperties;
import com.kuma.boot.mail.spring.template.JavaMailTemplate;
import com.kuma.boot.mail.spring.template.MailTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@AutoConfiguration(
   after = {MailSenderAutoConfiguration.class}
)
@EnableConfigurationProperties({MailProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.mail",
   name = {"enabled"},
   havingValue = "true"
)
public class HutoolMailAutoConfiguration implements InitializingBean {
   public HutoolMailAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(HutoolMailAutoConfiguration.class, "kuma-boot-starter-mail", new String[0]);
   }

   @Bean
   @ConditionalOnProperty(
      prefix = "spring.mail",
      name = {"host"}
   )
   @ConditionalOnBean({org.springframework.boot.mail.autoconfigure.MailProperties.class, JavaMailSender.class})
   public MailTemplate mailTemplate(JavaMailSender mailSender, org.springframework.boot.mail.autoconfigure.MailProperties mailProperties) {
      LogUtils.started(MailTemplate.class, "kuma-boot-starter-mail", new String[0]);
      return new JavaMailTemplate(mailSender, mailProperties);
   }

   @Bean
   @ConditionalOnProperty(
      prefix = "spring.mail",
      name = {"host"}
   )
   public MailAccount mailAccount(org.springframework.boot.mail.autoconfigure.MailProperties mailProperties) {
      MailAccount account = new MailAccount();
      account.setHost(mailProperties.getHost());
      account.setPort(mailProperties.getPort());
      account.setAuth(mailProperties.getPassword() != null);
      account.setPass(mailProperties.getPassword());
      account.setSocketFactoryPort(mailProperties.getPort());
      account.setStarttlsEnable(true);
      account.setSslEnable(true);
      return account;
   }

   @Bean
   @ConditionalOnProperty(
      prefix = "spring.mail",
      name = {"host"}
   )
   public MailUtils mailUtils(MailAccount mailAccount) {
      return new MailUtils(mailAccount);
   }
}
