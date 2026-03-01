/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingTalkMsgType;
import java.io.Serializable;
import java.util.List;

public class DingActionCard
extends DingTalkMessage {
    private ActionCard actionCard;

    public DingActionCard() {
        this.setMsgtype(DingTalkMsgType.ACTION_CARD.type());
    }

    public ActionCard getActionCard() {
        return this.actionCard;
    }

    public void setActionCard(ActionCard actionCard) {
        this.actionCard = actionCard;
    }

    public static class ActionCard
    implements Serializable {
        private String title;
        private String text;
        private String btnOrientation;
        private List<Button> btns;

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

        public List<Button> getBtns() {
            return this.btns;
        }

        public void setBtns(List<Button> btns) {
            this.btns = btns;
        }

        public static class Button
        implements Serializable {
            private String title;
            private String actionURL;

            public String getTitle() {
                return this.title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getActionURL() {
                return this.actionURL;
            }

            public void setActionURL(String actionURL) {
                this.actionURL = actionURL;
            }
        }
    }
}

