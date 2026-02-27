/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingTalkMsgType;
import java.io.Serializable;
import java.util.Map;

public class DingMarkDown
extends Message {
    private MarkDown markdown;

    public DingMarkDown(MarkDown markdown) {
        this.setMsgtype(DingTalkMsgType.MARKDOWN.type());
        this.markdown = markdown;
    }

    public MarkDown getMarkdown() {
        return this.markdown;
    }

    public void setMarkdown(MarkDown markdown) {
        this.markdown = markdown;
    }

    @Override
    public void transfer(Map<String, Object> params) {
        this.markdown.text = this.replaceContent(this.markdown.text, params);
    }

    public static class MarkDown
    implements Serializable {
        private String title;
        private String text;

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public MarkDown() {
        }

        public MarkDown(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }
}

