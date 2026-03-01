/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.trigger.model;

import java.io.Serializable;

public class TimeTriggerMsg
implements Serializable {
    private static final long serialVersionUID = 8897917127201859535L;
    private String triggerExecutor;
    private Long triggerTime;
    private Object param;
    private String uniqueKey;
    private String topic;
    private String type;

    public TimeTriggerMsg() {
    }

    public TimeTriggerMsg(String triggerExecutor, Long triggerTime, Object param, String uniqueKey, String topic) {
        this.triggerExecutor = triggerExecutor;
        this.triggerTime = triggerTime;
        this.param = param;
        this.uniqueKey = uniqueKey;
        this.topic = topic;
    }

    public String getTriggerExecutor() {
        return this.triggerExecutor;
    }

    public void setTriggerExecutor(String triggerExecutor) {
        this.triggerExecutor = triggerExecutor;
    }

    public Long getTriggerTime() {
        return this.triggerTime;
    }

    public void setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Object getParam() {
        return this.param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

