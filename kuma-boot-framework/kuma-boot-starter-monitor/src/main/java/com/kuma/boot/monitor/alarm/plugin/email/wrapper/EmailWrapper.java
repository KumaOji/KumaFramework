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

package com.kuma.boot.monitor.alarm.plugin.email.wrapper;

import com.kuma.boot.monitor.alarm.core.loader.entity.RegisterInfo;
import com.kuma.boot.monitor.alarm.core.loader.helper.RegisterInfoLoaderHelper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/** 选择email报警时，必须指定对应的帐号信息 */
public class EmailWrapper {

   public static HtmlEmail genEmailClient() throws EmailException {
      RegisterInfo info = RegisterInfoLoaderHelper.load();

      HtmlEmail htmlEmail = new HtmlEmail();
      htmlEmail.setCharset("UTF-8");
      htmlEmail.setHostName(info.getEmailHost());
      htmlEmail.setSmtpPort(info.getEmailPort());
      htmlEmail.setFrom(info.getEmailFrom());
      htmlEmail.setAuthentication(info.getEmailUname(), info.getEmailToken());
      if (BooleanUtils.isTrue(info.getEmailSsl())) {
         htmlEmail.setSSLOnConnect(true);
      }
      return htmlEmail;
   }
}
