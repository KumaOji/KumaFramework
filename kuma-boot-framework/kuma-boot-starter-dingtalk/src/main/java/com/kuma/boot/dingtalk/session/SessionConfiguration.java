/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.session;

import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;
import com.kuma.boot.dingtalk.model.DingerRobot;

public class SessionConfiguration {
    protected DingtalkProperties dingtalkProperties;
    protected DingerRobot dingerRobot;

    private SessionConfiguration(DingtalkProperties dingtalkProperties, DingerRobot dingerRobot) {
        this.dingtalkProperties = dingtalkProperties;
        this.dingerRobot = dingerRobot;
    }

    public static SessionConfiguration of(DingtalkProperties dingtalkProperties, DingerRobot dingerRobot) {
        return new SessionConfiguration(dingtalkProperties, dingerRobot);
    }

    public DingtalkProperties getDingerProperties() {
        return this.dingtalkProperties;
    }

    public DingerRobot getDingerRobot() {
        return this.dingerRobot;
    }
}

