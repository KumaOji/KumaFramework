/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingTalkMsgType;
import java.io.Serializable;
import java.util.Map;

public class DingText
extends Message {
    private Text text;

    public DingText(Text text) {
        this.setMsgtype(DingTalkMsgType.TEXT.type());
        this.text = text;
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    @Override
    public void transfer(Map<String, Object> params) {
        this.text.content = this.replaceContent(this.text.content, params);
    }

    public static class Text
    implements Serializable {
        private String content;

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Text() {
        }

        public Text(String content) {
            this.content = content;
        }
    }
}

