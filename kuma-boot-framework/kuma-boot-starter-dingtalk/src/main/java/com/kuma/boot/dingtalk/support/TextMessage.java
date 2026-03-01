/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.support;

import com.kuma.boot.dingtalk.entity.DingerRequest;

import java.text.MessageFormat;

public class TextMessage
implements CustomMessage {
    @Override
    public String message(String projectId, DingerRequest request) {
        String content = request.getContent();
        return MessageFormat.format("\u3010Dinger\u901a\u77e5\u3011 {0}\n- \u5185\u5bb9: {1}.", projectId, content);
    }
}

