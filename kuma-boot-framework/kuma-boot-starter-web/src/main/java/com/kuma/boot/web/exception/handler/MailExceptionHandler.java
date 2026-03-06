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

package com.kuma.boot.web.exception.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.exception.domain.ExceptionMessage;
import com.kuma.boot.web.exception.domain.ExceptionNoticeResponse;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.text.MessageFormat;

/** 异常邮件通知 */
public class MailExceptionHandler extends com.kuma.boot.web.exception.handler.AbstractExceptionHandler {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public MailExceptionHandler(
            Object mailProperties,
            ExceptionHandleProperties config,
            Object sender,
            String applicationName) {
        super(config, applicationName);
        this.mailSender = (JavaMailSender) sender;
        this.mailProperties = (MailProperties) mailProperties;
    }

    @Override
    public ExceptionNoticeResponse send( ExceptionMessage sendMessage) {
        String[] to = config.getReceiveEmails().toArray(new String[0]);

        sendEmail(to, sendMessage);

        ExceptionNoticeResponse response = new ExceptionNoticeResponse();
        response.setErrMsg("发送成功");
        response.setSuccess(true);

        return response;
    }

    @Override
    protected void initThread() {
        this.setName("kmc-mail-exception-task");
        this.start();
    }

    private void sendEmail(String[] to, ExceptionMessage message) {
        String title = "请求异常信息监控";
        String content =
                MessageFormat.format(
                        loadTemplate(),
                        message.getApplicationName(),
                        message.getTraceId(),
                        message.getIp(),
                        message.getRequestUri(),
                        message.getMessage(),
                        message.getNumber(),
                        message.getTime(),
                        message.getThreadId(),
                        message.getStack());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailProperties.getUsername());
            if (to == null || to.length <= 0) {
                helper.setTo(mailProperties.getUsername());
            } else {
                helper.setTo(to);
            }
            helper.setSubject(title);
            helper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            LogUtils.error("", e);
        }
    }

    private static String loadTemplate() {
        return "<h5>异常信息：</span><table border=\"1\" cellpadding=\"3\""
                + " style=\"border-collapse:collapse; width:80%;\" >\n"
                + "   <thead style=\"font-weight: bold;color: #ffffff;background-color:"
                + " #ff8c00;\" >      <tr>\n"
                + "         <td width=\"10%\" >服务名</td>\n"
                + "         <td width=\"10%\" >traceId</td>\n"
                + "         <td width=\"5%\" >ip</td>\n"
                + "         <td width=\"10%\" >请求地址</td>\n"
                + "         <td width=\"10%\" >消息</td>\n"
                + "         <td width=\"5%\" >数量</td>\n"
                + "         <td width=\"5%\" >最新触发时间</td>\n"
                + "         <td width=\"5%\" >线程id</td>\n"
                + "         <td width=\"40%\" >堆栈信息</td>\n"
                + "      </tr>\n"
                + "   </thead>\n"
                + "   <tbody>\n"
                + "      <tr>\n"
                + "         <td>{0}</td>\n"
                + "         <td>{1}</td>\n"
                + "         <td>{2}</td>\n"
                + "         <td>{3}</td>\n"
                + "         <td>{4}</td>\n"
                + "         <td>{5}</td>\n"
                + "         <td>{6}</td>\n"
                + "         <td>{7}</td>\n"
                + "         <td>{8}</td>\n"
                + "      </tr>\n"
                + "   </tbody>\n"
                + "</table>";
    }
}
