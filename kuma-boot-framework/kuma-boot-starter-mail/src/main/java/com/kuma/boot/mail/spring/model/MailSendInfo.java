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

package com.kuma.boot.mail.spring.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MailSendInfo {

   public MailSendInfo(MailDetails mailDetails) {
      this.mailDetails = mailDetails;
   }

   /** 邮件信息 */
   private final MailDetails mailDetails;

   /** 发送时间 */
   private LocalDateTime sentDate;

   /** 是否发送成功 */
   private Boolean success;

   /** 错误信息 errorMsg */
   private String errorMsg;
}
