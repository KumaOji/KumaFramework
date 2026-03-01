/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.support;

import com.kuma.boot.dingtalk.entity.DingerRequest;

import java.text.MessageFormat;
import java.util.List;

public class MarkDownMessage
implements CustomMessage {
    @Override
    public String message(String projectId, DingerRequest request) {
        String content = request.getContent();
        String title = request.getTitle();
        List<String> phones = request.getPhones();
        StringBuilder text = new StringBuilder(title);
        if (phones != null && !phones.isEmpty()) {
            for (String phone : phones) {
                text.append("@").append(phone);
            }
        }
        return MessageFormat.format("#### \u3010Dinger\u901a\u77e5\u3011 {0} \n - \u9879\u76ee\u540d\u79f0: {1}\n- \u5185\u5bb9: {2}", text, projectId, content);
    }
}

