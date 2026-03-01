/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import java.io.Serializable;
import java.util.List;

public class Message
extends DingTalkMessage
implements Serializable {
    private At at;

    public Message() {
    }

    public Message(At at) {
        this.at = at;
    }

    public At getAt() {
        return this.at;
    }

    public void setAt(At at) {
        this.at = at;
    }

    public static class At
    implements Serializable {
        private List<String> atMobiles;
        private Boolean isAtAll = false;

        public At() {
        }

        public At(List<String> atMobiles) {
            this.atMobiles = atMobiles;
        }

        public At(Boolean isAtAll) {
            this.isAtAll = isAtAll;
        }

        public At(List<String> atMobiles, Boolean isAtAll) {
            this.atMobiles = atMobiles;
            this.isAtAll = isAtAll;
        }

        public List<String> getAtMobiles() {
            return this.atMobiles;
        }

        public void setAtMobiles(List<String> atMobiles) {
            this.atMobiles = atMobiles;
        }

        public Boolean getIsAtAll() {
            return this.isAtAll;
        }

        public void setIsAtAll(Boolean atAll) {
            this.isAtAll = atAll;
        }
    }
}

