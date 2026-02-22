/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.security.spring.event.domain;

import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

public class DialogueMessage {
    @Schema(name="\u5bf9\u8bdd\u8be6\u60c5ID")
    private String detailId;
    @Schema(name="\u63a5\u6536\u4ebaID")
    private String receiverId;
    @Schema(name="\u63a5\u6536\u4eba\u540d\u79f0", title="\u5197\u4f59\u4fe1\u606f\uff0c\u589e\u52a0\u8be5\u5b57\u6bb5\u51cf\u5c11\u91cd\u590d\u67e5\u8be2")
    private String receiverName;
    @Schema(name="\u53d1\u9001\u4eba\u5934\u50cf")
    private String receiverAvatar;
    @Schema(name="\u516c\u544a\u5185\u5bb9")
    private String content;
    @Schema(name="\u5bf9\u8bddID")
    private String dialogueId;
    @Schema(name="\u53d1\u9001\u4ebaID")
    private String senderId;
    @Schema(name="\u53d1\u9001\u4eba\u540d\u79f0", title="\u5197\u4f59\u4fe1\u606f\uff0c\u589e\u52a0\u8be5\u5b57\u6bb5\u51cf\u5c11\u91cd\u590d\u67e5\u8be2")
    private String senderName;
    @Schema(name="\u53d1\u9001\u4eba\u5934\u50cf")
    private String senderAvatar;

    public String getDetailId() {
        return this.detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAvatar() {
        return this.receiverAvatar;
    }

    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = receiverAvatar;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDialogueId() {
        return this.dialogueId;
    }

    public void setDialogueId(String dialogueId) {
        this.dialogueId = dialogueId;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return this.senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("detailId", (Object)this.detailId).add("receiverId", (Object)this.receiverId).add("receiverName", (Object)this.receiverName).add("receiverAvatar", (Object)this.receiverAvatar).add("content", (Object)this.content).add("dialogueId", (Object)this.dialogueId).add("senderId", (Object)this.senderId).add("senderName", (Object)this.senderName).add("senderAvatar", (Object)this.senderAvatar).toString();
    }
}

