/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.entity;

import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.common.DateTimeUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MonitorBean
implements Comparable<MonitorBean> {
    private String app;
    private String id;
    private String name;
    private long monitor;
    private int pre;
    private int after;
    private String time;
    private String dateTime;
    private LocalDateTime localDateTime;

    public MonitorBean() {
    }

    public MonitorBean(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    public long getMonitor() {
        return this.monitor;
    }

    public void setMonitor(long monitor) {
        this.monitor = monitor;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public int compareTo(MonitorBean o) {
        return this.getLocalDateTime().compareTo(o.getLocalDateTime());
    }

    public String getApp() {
        return this.app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPre() {
        return this.pre;
    }

    public void setPre(int pre) {
        this.pre = pre;
    }

    public int getAfter() {
        return this.after;
    }

    public void setAfter(int after) {
        this.after = after;
    }

    public String getTime() {
        return this.localDateTime != null ? this.localDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "";
    }

    public String getDateTime() {
        return DateTimeUtil.toString(this.localDateTime);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

