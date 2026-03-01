/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.cloud.stream.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kuma.cloud.stream.rocketmq.custom")
public class RocketmqCustomProperties {
    private String promotionTopic;
    private String promotionGroup;
    private String orderTopic;
    private String orderGroup;
    private String msgExtTopic;
    private String msgExtGroup;
    private String goodsTopic;
    private String goodsGroup;
    private String topicUser;
    private String memberTopic;
    private String memberGroup;
    private String otherTopic;
    private String otherGroup;
    private String noticeTopic;
    private String noticeGroup;
    private String noticeSendTopic;
    private String noticeSendGroup;
    private String storeTopic;
    private String storeGroup;
    private String afterSaleTopic;
    private String afterSaleGroup;
    private String broadcastTopic;
    private String broadcastGroup;

    public String getPromotionTopic() {
        return this.promotionTopic;
    }

    public void setPromotionTopic(String promotionTopic) {
        this.promotionTopic = promotionTopic;
    }

    public String getPromotionGroup() {
        return this.promotionGroup;
    }

    public void setPromotionGroup(String promotionGroup) {
        this.promotionGroup = promotionGroup;
    }

    public String getOrderTopic() {
        return this.orderTopic;
    }

    public void setOrderTopic(String orderTopic) {
        this.orderTopic = orderTopic;
    }

    public String getOrderGroup() {
        return this.orderGroup;
    }

    public void setOrderGroup(String orderGroup) {
        this.orderGroup = orderGroup;
    }

    public String getMsgExtTopic() {
        return this.msgExtTopic;
    }

    public void setMsgExtTopic(String msgExtTopic) {
        this.msgExtTopic = msgExtTopic;
    }

    public String getMsgExtGroup() {
        return this.msgExtGroup;
    }

    public void setMsgExtGroup(String msgExtGroup) {
        this.msgExtGroup = msgExtGroup;
    }

    public String getGoodsTopic() {
        return this.goodsTopic;
    }

    public void setGoodsTopic(String goodsTopic) {
        this.goodsTopic = goodsTopic;
    }

    public String getGoodsGroup() {
        return this.goodsGroup;
    }

    public void setGoodsGroup(String goodsGroup) {
        this.goodsGroup = goodsGroup;
    }

    public String getTopicUser() {
        return this.topicUser;
    }

    public void setTopicUser(String topicUser) {
        this.topicUser = topicUser;
    }

    public String getMemberTopic() {
        return this.memberTopic;
    }

    public void setMemberTopic(String memberTopic) {
        this.memberTopic = memberTopic;
    }

    public String getMemberGroup() {
        return this.memberGroup;
    }

    public void setMemberGroup(String memberGroup) {
        this.memberGroup = memberGroup;
    }

    public String getOtherTopic() {
        return this.otherTopic;
    }

    public void setOtherTopic(String otherTopic) {
        this.otherTopic = otherTopic;
    }

    public String getOtherGroup() {
        return this.otherGroup;
    }

    public void setOtherGroup(String otherGroup) {
        this.otherGroup = otherGroup;
    }

    public String getNoticeTopic() {
        return this.noticeTopic;
    }

    public void setNoticeTopic(String noticeTopic) {
        this.noticeTopic = noticeTopic;
    }

    public String getNoticeGroup() {
        return this.noticeGroup;
    }

    public void setNoticeGroup(String noticeGroup) {
        this.noticeGroup = noticeGroup;
    }

    public String getNoticeSendTopic() {
        return this.noticeSendTopic;
    }

    public void setNoticeSendTopic(String noticeSendTopic) {
        this.noticeSendTopic = noticeSendTopic;
    }

    public String getNoticeSendGroup() {
        return this.noticeSendGroup;
    }

    public void setNoticeSendGroup(String noticeSendGroup) {
        this.noticeSendGroup = noticeSendGroup;
    }

    public String getStoreTopic() {
        return this.storeTopic;
    }

    public void setStoreTopic(String storeTopic) {
        this.storeTopic = storeTopic;
    }

    public String getStoreGroup() {
        return this.storeGroup;
    }

    public void setStoreGroup(String storeGroup) {
        this.storeGroup = storeGroup;
    }

    public String getAfterSaleTopic() {
        return this.afterSaleTopic;
    }

    public void setAfterSaleTopic(String afterSaleTopic) {
        this.afterSaleTopic = afterSaleTopic;
    }

    public String getAfterSaleGroup() {
        return this.afterSaleGroup;
    }

    public void setAfterSaleGroup(String afterSaleGroup) {
        this.afterSaleGroup = afterSaleGroup;
    }

    public String getBroadcastTopic() {
        return this.broadcastTopic;
    }

    public void setBroadcastTopic(String broadcastTopic) {
        this.broadcastTopic = broadcastTopic;
    }

    public String getBroadcastGroup() {
        return this.broadcastGroup;
    }

    public void setBroadcastGroup(String broadcastGroup) {
        this.broadcastGroup = broadcastGroup;
    }
}

