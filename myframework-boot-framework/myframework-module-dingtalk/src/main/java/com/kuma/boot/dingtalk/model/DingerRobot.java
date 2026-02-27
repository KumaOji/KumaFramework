/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.BeanUtils
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.entity.DingerRequest;
import com.kuma.boot.dingtalk.entity.DingerResponse;
import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.enums.DingerResponseCodeEnum;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.MediaTypeEnum;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.exception.AsyncCallException;
import com.kuma.boot.dingtalk.exception.SendMsgException;
import com.kuma.boot.dingtalk.support.CustomMessage;
import com.kuma.boot.dingtalk.support.SignBase;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;

public class DingerRobot
extends AbstractDingerSender {
    public DingerRobot(DingtalkProperties dingtalkProperties, DingerManagerBuilder dingTalkManagerBuilder) {
        super(dingtalkProperties, dingTalkManagerBuilder);
    }

    @Override
    public DingerResponse send(MessageSubType messageSubType, DingerRequest request) {
        return this.send(this.dingtalkProperties.getDefaultDinger(), messageSubType, request);
    }

    @Override
    public DingerResponse send(DingerType dingerType, MessageSubType messageSubType, DingerRequest request) {
        if (!messageSubType.isSupport()) {
            return DingerResponse.failed(DingerResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED);
        }
        CustomMessage customMessage = this.customMessage(messageSubType);
        String msgContent = customMessage.message(this.dingtalkProperties.getProjectId(), request);
        request.setContent(msgContent);
        MsgType msgType = messageSubType.msgType(dingerType, request);
        return this.send(msgType);
    }

    protected <T extends MsgType> DingerResponse send(T message) {
        DingerType dingerType = message.getDingerType();
        String dkid = this.dingTalkManagerBuilder.getDingerIdGenerator().dingerId();
        Map<DingerType, DingtalkProperties.Dinger> dingers = this.dingtalkProperties.getDingers();
        if (!this.dingtalkProperties.getEnabled() || !dingers.containsKey((Object)dingerType)) {
            return DingerResponse.failed(dkid, DingerResponseCodeEnum.DINGER_DISABLED);
        }
        DingerConfig localDinger = DingerRobot.getLocalDinger();
        boolean dingerConfig = localDinger != null;
        try {
            DingtalkProperties.Dinger dinger;
            if (dingerConfig) {
                dinger = new DingtalkProperties.Dinger();
                BeanUtils.copyProperties((Object)localDinger, (Object)dinger);
                dinger.setAsync(localDinger.getAsyncExecute());
                dinger.setRobotUrl(dingers.get((Object)dingerType).getRobotUrl());
            } else {
                dinger = dingers.get((Object)dingerType);
            }
            StringBuilder webhook = new StringBuilder();
            webhook.append(dinger.getRobotUrl()).append("=").append(dinger.getTokenId());
            LogUtils.info((String)"dingerId={} send message and use dinger={}, tokenId={}.", (Object[])new Object[]{dkid, dingerType, dinger.getTokenId()});
            if (dingerType == DingerType.DINGTALK && DingerUtils.isNotEmpty(dinger.getSecret())) {
                Object sign = this.dingTalkManagerBuilder.getDingerSignAlgorithm().sign(dinger.getSecret().trim());
                webhook.append(((SignBase)sign).transfer());
            }
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", MediaTypeEnum.JSON.type());
            if (dinger.isAsync()) {
                this.dingTalkManagerBuilder.getDingTalkExecutor().execute(() -> {
                    try {
                        String result = this.dingTalkManagerBuilder.getDingerHttpClient().post(webhook.toString(), (Map<String, String>)headers, message);
                        this.dingTalkManagerBuilder.getDingerAsyncCallback().execute(dkid, result);
                    }
                    catch (Exception e) {
                        this.exceptionCallback(dkid, message, new AsyncCallException(e));
                    }
                });
                return DingerResponse.success(dkid, dkid);
            }
            String response = this.dingTalkManagerBuilder.getDingerHttpClient().post(webhook.toString(), headers, message);
            LogUtils.info((String)response, (Object[])new Object[0]);
            return DingerResponse.success(dkid, response);
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
            this.exceptionCallback(dkid, message, new SendMsgException(e));
            return DingerResponse.failed(dkid, DingerResponseCodeEnum.SEND_MESSAGE_FAILED);
        }
    }
}

