/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.mail.internet.MimeMessage
 *  org.springframework.boot.mail.autoconfigure.MailProperties
 *  org.springframework.mail.javamail.JavaMailSender
 *  org.springframework.mail.javamail.MimeMessageHelper
 */
package com.kuma.boot.web.exception.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.exception.domain.ExceptionMessage;
import com.kuma.boot.web.exception.domain.ExceptionNoticeResponse;
import com.kuma.boot.web.exception.properties.ExceptionHandleProperties;
import jakarta.mail.internet.MimeMessage;
import java.text.MessageFormat;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailExceptionHandler
extends AbstractExceptionHandler {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public MailExceptionHandler(MailProperties mailProperties, ExceptionHandleProperties config, JavaMailSender sender, String applicationName) {
        super(config, applicationName);
        this.mailSender = sender;
        this.mailProperties = mailProperties;
    }

    @Override
    public ExceptionNoticeResponse send(ExceptionMessage sendMessage) {
        String[] to = this.config.getReceiveEmails().toArray(new String[0]);
        this.sendEmail(to, sendMessage);
        ExceptionNoticeResponse response = new ExceptionNoticeResponse();
        response.setErrMsg("\u53d1\u9001\u6210\u529f");
        response.setSuccess(true);
        return response;
    }

    @Override
    protected void initThread() {
        this.setName("kmc-mail-exception-task");
        this.start();
    }

    private void sendEmail(String[] to, ExceptionMessage message) {
        String title = "\u8bf7\u6c42\u5f02\u5e38\u4fe1\u606f\u76d1\u63a7";
        String content = MessageFormat.format(MailExceptionHandler.loadTemplate(), message.getApplicationName(), message.getTraceId(), message.getIp(), message.getRequestUri(), message.getMessage(), message.getNumber(), message.getTime(), message.getThreadId(), message.getStack());
        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(this.mailProperties.getUsername());
            if (to == null || to.length <= 0) {
                helper.setTo(this.mailProperties.getUsername());
            } else {
                helper.setTo(to);
            }
            helper.setSubject(title);
            helper.setText(content, true);
            this.mailSender.send(mimeMessage);
        }
        catch (Exception e) {
            LogUtils.error((String)"", (Object[])new Object[]{e});
        }
    }

    private static String loadTemplate() {
        return "<h5>\u5f02\u5e38\u4fe1\u606f\uff1a</span><table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >      <tr>\n         <td width=\"10%\" >\u670d\u52a1\u540d</td>\n         <td width=\"10%\" >traceId</td>\n         <td width=\"5%\" >ip</td>\n         <td width=\"10%\" >\u8bf7\u6c42\u5730\u5740</td>\n         <td width=\"10%\" >\u6d88\u606f</td>\n         <td width=\"5%\" >\u6570\u91cf</td>\n         <td width=\"5%\" >\u6700\u65b0\u89e6\u53d1\u65f6\u95f4</td>\n         <td width=\"5%\" >\u7ebf\u7a0bid</td>\n         <td width=\"40%\" >\u5806\u6808\u4fe1\u606f</td>\n      </tr>\n   </thead>\n   <tbody>\n      <tr>\n         <td>{0}</td>\n         <td>{1}</td>\n         <td>{2}</td>\n         <td>{3}</td>\n         <td>{4}</td>\n         <td>{5}</td>\n         <td>{6}</td>\n         <td>{7}</td>\n         <td>{8}</td>\n      </tr>\n   </tbody>\n</table>";
    }
}

