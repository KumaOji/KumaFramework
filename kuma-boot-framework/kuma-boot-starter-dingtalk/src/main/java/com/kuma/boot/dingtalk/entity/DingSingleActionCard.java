/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingTalkMsgType;
import java.io.Serializable;

public class DingSingleActionCard
extends DingTalkMessage {
    private SingleActionCard actionCard;

    public DingSingleActionCard() {
        this.setMsgtype(DingTalkMsgType.ACTION_CARD.type());
    }

    public SingleActionCard getActionCard() {
        return this.actionCard;
    }

    public void setActionCard(SingleActionCard actionCard) {
        this.actionCard = actionCard;
    }

    public static class SingleActionCard
    implements Serializable {
        private String title;
        private String text;
        private String btnOrientation;
        private String singleTitle;
        private String singleURL;

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

        public String getBtnOrientation() {
            return this.btnOrientation;
        }

        public void setBtnOrientation(String btnOrientation) {
            this.btnOrientation = btnOrientation;
        }

        public String getSingleTitle() {
            return this.singleTitle;
        }

        public void setSingleTitle(String singleTitle) {
            this.singleTitle = singleTitle;
        }

        public String getSingleURL() {
            return this.singleURL;
        }

        public void setSingleURL(String singleURL) {
            this.singleURL = singleURL;
        }
    }
}

