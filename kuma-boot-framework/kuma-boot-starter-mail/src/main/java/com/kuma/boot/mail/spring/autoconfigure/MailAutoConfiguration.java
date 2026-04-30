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

package com.kuma.boot.mail.spring.autoconfigure;

import com.kuma.boot.mail.spring.sender.MailSender;
import com.kuma.boot.mail.spring.sender.MailSenderImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@AutoConfiguration(after = MailSenderAutoConfiguration.class)
@EnableConfigurationProperties({com.kuma.boot.mail.hutool.autoconfigure.properties.MailProperties.class})
@ConditionalOnProperty(
        prefix = com.kuma.boot.mail.hutool.autoconfigure.properties.MailProperties.PREFIX,
        name = "enabled",
        havingValue = "true")
public class MailAutoConfiguration {

   @Bean
   @ConditionalOnMissingBean(MailSender.class)
   @ConditionalOnProperty(prefix = "spring.mail", name = "host")
   public MailSender mailSenderImpl(
           JavaMailSender javaMailSender,
           ApplicationEventPublisher applicationEventPublisher,
           MailProperties mailProperties) {
      return new MailSenderImpl(javaMailSender, applicationEventPublisher, mailProperties);
   }
}
