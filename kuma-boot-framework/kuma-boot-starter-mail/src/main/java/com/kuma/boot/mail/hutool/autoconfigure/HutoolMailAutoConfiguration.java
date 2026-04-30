/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.mail.hutool.autoconfigure;

import cn.hutool.extra.mail.MailAccount;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.mail.hutool.MailUtils;
import com.kuma.boot.mail.spring.template.JavaMailTemplate;
import com.kuma.boot.mail.spring.template.MailTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * MailAutoConfiguration 基于hutool的mail 需要加上 javax.mail:javax.mail-api:1.6.2
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-09 11:38:47
 */
@AutoConfiguration(after = MailSenderAutoConfiguration.class)
@EnableConfigurationProperties({com.kuma.boot.mail.hutool.autoconfigure.properties.MailProperties.class})
@ConditionalOnProperty(
        prefix = com.kuma.boot.mail.hutool.autoconfigure.properties.MailProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
public class HutoolMailAutoConfiguration implements InitializingBean {

   @Override
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(HutoolMailAutoConfiguration.class, StarterNameConstants.MAIL_STARTER);
   }

   @Bean
   @ConditionalOnProperty(prefix = "spring.mail", name = "host")
   @ConditionalOnBean({MailProperties.class, JavaMailSender.class})
   public MailTemplate mailTemplate(JavaMailSender mailSender, MailProperties mailProperties) {
      LogUtils.started(MailTemplate.class, StarterNameConstants.MAIL_STARTER);

      return new JavaMailTemplate(mailSender, mailProperties);
   }

   @Bean
   @ConditionalOnProperty(prefix = "spring.mail", name = "host")
   public MailAccount mailAccount(MailProperties mailProperties) {
      MailAccount account = new MailAccount();
      account.setHost(mailProperties.getHost());
      account.setPort(mailProperties.getPort());
      account.setAuth(mailProperties.getPassword() != null);
      // account.setFrom(mailProperties.getFrom());
      // account.setUser(mailProperties.getUser());
      account.setPass(mailProperties.getPassword());
      account.setSocketFactoryPort(mailProperties.getPort());

      account.setStarttlsEnable(true);
      account.setSslEnable(true);
      // account.setTimeout(mailProperties.getTimeout());
      // account.setConnectionTimeout(mailProperties.getConnectionTimeout());
      return account;
   }

   @Bean
   @ConditionalOnProperty(prefix = "spring.mail", name = "host")
   public MailUtils mailUtils(MailAccount mailAccount) {
      return new MailUtils(mailAccount);
   }
}
