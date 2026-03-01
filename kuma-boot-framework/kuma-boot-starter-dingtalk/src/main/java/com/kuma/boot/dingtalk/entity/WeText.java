/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.WeTalkMsgType;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WeText
extends WeTalkMessage {
    private Text text;

    public WeText(Text text) {
        this.setMsgtype(WeTalkMsgType.TEXT.type());
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
        private List<String> mentioned_list;
        private List<String> mentioned_mobile_list;

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getMentioned_list() {
            return this.mentioned_list;
        }

        public void setMentioned_list(List<String> mentioned_list) {
            this.mentioned_list = mentioned_list;
        }

        public List<String> getMentioned_mobile_list() {
            return this.mentioned_mobile_list;
        }

        public void setMentioned_mobile_list(List<String> mentioned_mobile_list) {
            this.mentioned_mobile_list = mentioned_mobile_list;
        }

        public Text() {
        }

        public Text(String content) {
            this.content = content;
        }
    }
}

