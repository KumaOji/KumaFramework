/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.http;

import java.io.Serializable;

public class IpAnalyzePoint
implements Serializable {
    private String x;
    private String y;

    public String getX() {
        return this.x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return this.y;
    }

    public void setY(String y) {
        this.y = y;
    }
}

