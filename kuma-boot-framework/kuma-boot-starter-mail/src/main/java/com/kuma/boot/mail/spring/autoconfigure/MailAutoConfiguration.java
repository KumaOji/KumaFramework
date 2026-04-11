package com.kuma.boot.mail.spring.autoconfigure;

import com.kuma.boot.mail.hutool.autoconfigure.properties.MailProperties;
import com.kuma.boot.mail.spring.sender.MailSender;
import com.kuma.boot.mail.spring.sender.MailSenderImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
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
public class MailAutoConfiguration {
   public MailAutoConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean({MailSender.class})
   @ConditionalOnProperty(
      prefix = "spring.mail",
      name = {"host"}
   )
   public MailSender mailSenderImpl(JavaMailSender javaMailSender, ApplicationEventPublisher applicationEventPublisher, org.springframework.boot.mail.autoconfigure.MailProperties mailProperties) {
      return new MailSenderImpl(javaMailSender, applicationEventPublisher, mailProperties);
   }
}
