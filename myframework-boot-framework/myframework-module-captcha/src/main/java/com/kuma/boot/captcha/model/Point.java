/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Point {
    private String secretKey;
    public int x;
    public int y;

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point(int x, int y, String secretKey) {
        this.secretKey = secretKey;
        this.x = x;
        this.y = y;
    }

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toJsonString() {
        return String.format("{\"secretKey\":\"%s\",\"x\":%d,\"y\":%d}", this.secretKey, this.x, this.y);
    }

    public Point parse(String jsonStr) {
        HashMap m = new HashMap();
        Arrays.stream(jsonStr.replaceFirst(",\\{", "\\{").replaceFirst("\\{", "").replaceFirst("\\}", "").replaceAll("\"", "").split(",")).forEach(item -> m.put(item.split(":")[0], item.split(":")[1]));
        this.setX(Double.valueOf(String.valueOf(m.get("x"))).intValue());
        this.setY(Double.valueOf(String.valueOf(m.get("y"))).intValue());
        this.setSecretKey(String.valueOf(m.getOrDefault("secretKey", "")));
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Point point = (Point)o;
        return this.x == point.x && this.y == point.y && Objects.equals(this.secretKey, point.secretKey);
    }

    public int hashCode() {
        return Objects.hash(this.secretKey, this.x, this.y);
    }
}

