/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.DingTalkMsgType;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DingFeedCard
extends DingTalkMessage {
    private FeedCard feedCard;

    public DingFeedCard() {
        this.setMsgtype(DingTalkMsgType.FEED_CARD.type());
    }

    public DingFeedCard(List<FeedCard.Link> links) {
        this();
        this.feedCard = new FeedCard(links);
    }

    public FeedCard getFeedCard() {
        return this.feedCard;
    }

    public void setFeedCard(FeedCard feedCard) {
        this.feedCard = feedCard;
    }

    @Override
    public void transfer(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof List)) continue;
            List<?> imageTexts = (List<?>) value;
            for (Object item : imageTexts) {
                ImageTextDeo imageText = (ImageTextDeo) item;
                this.feedCard.links.add(new FeedCard.Link(imageText.getTitle(), imageText.getUrl(), imageText.getPicUrl()));
            }
        }
    }

    public static class FeedCard
    implements Serializable {
        private List<Link> links;

        public FeedCard() {
        }

        public FeedCard(List<Link> links) {
            this.links = links;
        }

        public List<Link> getLinks() {
            return this.links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }

        public static class Link
        implements Serializable {
            private String title;
            private String messageURL;
            private String picURL;

            public Link() {
            }

            public Link(String title, String messageURL, String picURL) {
                this.title = title;
                this.messageURL = messageURL;
                this.picURL = picURL;
            }

            public String getTitle() {
                return this.title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMessageURL() {
                return this.messageURL;
            }

            public void setMessageURL(String messageURL) {
                this.messageURL = messageURL;
            }

            public String getPicURL() {
                return this.picURL;
            }

            public void setPicURL(String picURL) {
                this.picURL = picURL;
            }
        }
    }
}

