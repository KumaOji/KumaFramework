/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.trigger.message;

public class BroadcastMessage {
    private Long studioId;
    private String status;

    public BroadcastMessage(Long studioId, String status) {
        this.studioId = studioId;
        this.status = status;
    }

    public Long getStudioId() {
        return this.studioId;
    }

    public void setStudioId(Long studioId) {
        this.studioId = studioId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

