/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingerType;
import java.io.Serializable;
import java.util.Map;

public class MsgType
implements Serializable {
    private static final String PREFIX_TAG = "\\$\\{";
    private static final String SUFFIX_TAG = "}";
    private volatile DingerType dingerType;
    private String msgtype;

    public DingerType getDingerType() {
        return this.dingerType;
    }

    public void setDingerType(DingerType dingerType) {
        this.dingerType = dingerType;
    }

    public String getMsgtype() {
        return this.msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public void transfer(Map<String, Object> params) {
    }

    protected String replaceContent(String content, Map<String, Object> params) {
        for (String k : params.keySet()) {
            Object v = params.get(k);
            String key = PREFIX_TAG + k + SUFFIX_TAG;
            if (!(v instanceof CharSequence) && !(v instanceof Character) && !(v instanceof Boolean) && !(v instanceof Number)) continue;
            content = content.replaceAll(key, v.toString());
        }
        return content;
    }
}

