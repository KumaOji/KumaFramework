package com.kuma.boot.sms.tencent.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sms.common.handler.AbstractSendHandler;
import com.kuma.boot.sms.common.model.NoticeData;
import com.kuma.boot.sms.tencent.properties.TencentSmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 腾讯云短信发送 Handler
 *
 * <p>模板 {@code type} → {@link TencentSmsProperties#getTemplates()} 取模板 ID；
 * 参数按 {@link TencentSmsProperties#getParamsOrder(String)} 指定的顺序从
 * {@code NoticeData.params} 取值，组成腾讯云所需的有序 {@code TemplateParamSet}。
 */
public class TencentSendHandler extends AbstractSendHandler {

    public static final String CHANNEL_NAME = "TENCENT";

    private final TencentSmsProperties tencentProperties;
    private final SmsClient client;

    public TencentSendHandler(TencentSmsProperties properties, @Nullable ApplicationEventPublisher eventPublisher) {
        super(properties, eventPublisher);
        this.tencentProperties = properties;
        Credential cred = new Credential(properties.getSecretId(), properties.getSecretKey());
        this.client = new SmsClient(cred, properties.getRegion());
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean send(NoticeData noticeData, Collection phones) {
        String templateId = (String) this.tencentProperties.getTemplates(noticeData.getType());
        if (templateId == null) {
            LogUtils.debug("腾讯云短信未配置模板, type: {}", new Object[]{noticeData.getType()});
            return false;
        }

        SendSmsRequest request = new SendSmsRequest();
        request.setSmsSdkAppId(this.tencentProperties.getSdkAppId());
        request.setSignName(this.tencentProperties.getSignName());
        request.setTemplateId(templateId);
        request.setPhoneNumberSet(buildPhoneNumberSet((Collection<String>) phones));
        request.setTemplateParamSet(buildTemplateParamSet(noticeData));

        try {
            SendSmsResponse response = this.client.SendSms(request);
            boolean success = isAllSuccess(response);
            if (success) {
                this.publishSendSuccessEvent(noticeData, phones, response);
            } else {
                this.publishSendFailEvent(noticeData, phones, new RuntimeException(firstErrorMessage(response)), response);
            }
            return success;
        } catch (Exception e) {
            LogUtils.error(e);
            this.publishSendFailEvent(noticeData, phones, e, null);
            return false;
        }
    }

    /** 手机号补全区号（已带 + 前缀的保持原样） */
    private String[] buildPhoneNumberSet(Collection<String> phones) {
        String regionCode = this.tencentProperties.getDefaultRegionCode();
        return phones.stream()
                .map(phone -> phone.startsWith("+") ? phone : regionCode + phone)
                .toArray(String[]::new);
    }

    /** 按 paramsOrders 指定顺序组装有序参数数组；未配置顺序则返回空数组 */
    private String[] buildTemplateParamSet(NoticeData noticeData) {
        if (this.tencentProperties.getParamsOrders() == null) {
            return new String[0];
        }
        List<String> order = this.tencentProperties.getParamsOrder(noticeData.getType());
        if (order == null || order.isEmpty()) {
            return new String[0];
        }
        Map<String, Object> params = noticeData.getParams();
        List<String> values = new ArrayList<>(order.size());
        for (String key : order) {
            Object value = params == null ? null : params.get(key);
            values.add(value == null ? "" : String.valueOf(value));
        }
        return values.toArray(new String[0]);
    }

    private boolean isAllSuccess(SendSmsResponse response) {
        SendStatus[] statusSet = response.getSendStatusSet();
        if (statusSet == null || statusSet.length == 0) {
            return false;
        }
        for (SendStatus status : statusSet) {
            if (!"Ok".equalsIgnoreCase(status.getCode())) {
                return false;
            }
        }
        return true;
    }

    private String firstErrorMessage(SendSmsResponse response) {
        SendStatus[] statusSet = response.getSendStatusSet();
        if (statusSet == null || statusSet.length == 0) {
            return "empty SendStatusSet";
        }
        for (SendStatus status : statusSet) {
            if (!"Ok".equalsIgnoreCase(status.getCode())) {
                return status.getCode() + ": " + status.getMessage();
            }
        }
        return "unknown";
    }
}
