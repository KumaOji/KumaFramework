/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.WeTalkMsgType;
import java.io.Serializable;
import java.util.Map;

public class WeMarkdown
extends WeTalkMessage {
    private Markdown markdown;

    public WeMarkdown(Markdown markdown) {
        this.setMsgtype(WeTalkMsgType.MARKDOWN.type());
        this.markdown = markdown;
    }

    public Markdown getMarkdown() {
        return this.markdown;
    }

    public void setMarkdown(Markdown markdown) {
        this.markdown = markdown;
    }

    @Override
    public void transfer(Map<String, Object> params) {
        this.markdown.content = this.replaceContent(this.markdown.content, params);
    }

    public static class Markdown
    implements Serializable {
        private String content;

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Markdown() {
        }

        public Markdown(String content) {
            this.content = content;
        }
    }
}

