/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

import com.kuma.boot.dingtalk.entity.DingFeedCard;
import com.kuma.boot.dingtalk.entity.DingLink;
import com.kuma.boot.dingtalk.entity.DingMarkDown;
import com.kuma.boot.dingtalk.entity.DingText;
import com.kuma.boot.dingtalk.entity.DingerRequest;
import com.kuma.boot.dingtalk.entity.Message;
import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.entity.WeMarkdown;
import com.kuma.boot.dingtalk.entity.WeNews;
import com.kuma.boot.dingtalk.entity.WeText;
import com.kuma.boot.dingtalk.exception.DingerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum MessageSubType {
    TEXT(true){

        @Override
        public MsgType msgType(DingerType dingerType, DingerRequest request) {
            String content = request.getContent();
            boolean atAll = request.isAtAll();
            List<String> phones = request.getPhones();
            if (dingerType == DingerType.DINGTALK) {
                DingText message = new DingText(new DingText.Text(content));
                if (atAll) {
                    message.setAt(new Message.At(true));
                } else if (phones != null && !phones.isEmpty()) {
                    message.setAt(new Message.At(phones));
                }
                return message;
            }
            WeText.Text text = new WeText.Text(content);
            WeText weText = new WeText(text);
            if (atAll) {
                text.setMentioned_mobile_list(List.of("@all"));
            } else if (phones != null && !phones.isEmpty()) {
                text.setMentioned_mobile_list(phones);
            }
            return weText;
        }
    }
    ,
    MARKDOWN(true){

        @Override
        public MsgType msgType(DingerType dingerType, DingerRequest request) {
            String content = request.getContent();
            String title = request.getTitle();
            List<String> phones = request.getPhones();
            if (dingerType == DingerType.DINGTALK) {
                DingMarkDown message = new DingMarkDown(new DingMarkDown.MarkDown(title, content));
                if (!phones.isEmpty()) {
                    message.setAt(new Message.At(phones));
                }
                return message;
            }
            WeMarkdown.Markdown markdown = new WeMarkdown.Markdown(content);
            WeMarkdown weMarkdown = new WeMarkdown(markdown);
            return weMarkdown;
        }
    }
    ,
    IMAGETEXT(false){

        @Override
        public MsgType msgType(DingerType dingerType, DingerRequest request) {
            if (dingerType == DingerType.DINGTALK) {
                return new DingFeedCard(new ArrayList<DingFeedCard.FeedCard.Link>());
            }
            return new WeNews(new ArrayList<WeNews.News.Article>());
        }
    }
    ,
    LINK(false){

        @Override
        public MsgType msgType(DingerType dingerType, DingerRequest request) {
            if (dingerType == DingerType.DINGTALK) {
                return new DingLink();
            }
            throw new DingerException(ExceptionEnum.DINGER_UNSUPPORT_MESSAGE_TYPE_EXCEPTION, new Object[]{dingerType, this.name()});
        }
    };

    private boolean support;

    private MessageSubType(boolean support) {
        this.support = support;
    }

    public boolean isSupport() {
        return this.support;
    }

    public abstract MsgType msgType(DingerType var1, DingerRequest var2);

    public static boolean contains(String value) {
        return Arrays.stream(MessageSubType.values()).filter(e -> Objects.equals(e.name(), value)).count() > 0L;
    }
}

