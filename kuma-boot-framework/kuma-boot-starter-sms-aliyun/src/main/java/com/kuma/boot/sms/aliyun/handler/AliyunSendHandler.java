package com.kuma.boot.sms.aliyun.handler;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.aliyun.properties.AliyunSmsProperties;
import com.kuma.boot.sms.common.handler.AbstractSendHandler;
import com.kuma.boot.sms.common.model.NoticeData;
import java.util.Collection;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 阿里云短信发送 Handler
 *
 * <p>通过模板 {@code type} 在 {@link AliyunSmsProperties#getTemplates()} 中查找对应的模板 Code，
 * 将 {@link NoticeData#getParams()} 序列化为阿里云 TemplateParam(JSON) 后发送。
 */
public class AliyunSendHandler extends AbstractSendHandler {

    public static final String CHANNEL_NAME = "ALIYUN";

    private final AliyunSmsProperties aliyunProperties;
    private final Gson gson = new Gson();
    private final Client client;

    public AliyunSendHandler(AliyunSmsProperties properties, @Nullable ApplicationEventPublisher eventPublisher) {
        super(properties, eventPublisher);
        this.aliyunProperties = properties;
        this.client = buildClient(properties);
    }

    private static Client buildClient(AliyunSmsProperties properties) {
        try {
            Config config = new Config()
                    .setAccessKeyId(properties.getAccessKeyId())
                    .setAccessKeySecret(properties.getAccessKeySecret());
            config.endpoint = properties.getEndpoint();
            return new Client(config);
        } catch (Exception e) {
            throw new IllegalStateException("阿里云短信 Client 初始化失败", e);
        }
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean send(NoticeData noticeData, Collection phones) {
        String templateCode = (String) this.aliyunProperties.getTemplates(noticeData.getType());
        if (templateCode == null) {
            LogUtils.debug("阿里云短信未配置模板, type: {}", new Object[]{noticeData.getType()});
            return false;
        }

        SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(String.join(",", (Collection<String>) phones))
                .setSignName(this.aliyunProperties.getSignName())
                .setTemplateCode(templateCode)
                .setTemplateParam(this.gson.toJson(noticeData.getParams()));

        try {
            SendSmsResponse response = this.client.sendSms(request);
            boolean success = response.getBody() != null && "OK".equals(response.getBody().getCode());
            if (success) {
                this.publishSendSuccessEvent(noticeData, phones, response);
            } else {
                String msg = response.getBody() == null ? "empty body" : response.getBody().getMessage();
                this.publishSendFailEvent(noticeData, phones, new RuntimeException(msg), response);
            }
            return success;
        } catch (Exception e) {
            LogUtils.error(e);
            this.publishSendFailEvent(noticeData, phones, e, null);
            return false;
        }
    }
}
