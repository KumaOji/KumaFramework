/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.exception.domain;

public class ExceptionMessage {
    private String traceId;
    private String message;
    private int number;
    private String stack;
    private String time;
    private String mac;
    private long threadId;
    private String applicationName;
    private String hostname;
    private String ip;
    private String requestUri;

    public ExceptionMessage increment() {
        ++this.number;
        return this;
    }

    public String getTraceId() {
        return this.traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStack() {
        return this.stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public long getThreadId() {
        return this.threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestUri() {
        return this.requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String toString() {
        return "\u670d\u52a1\u540d\u79f0\uff1a" + this.applicationName + "\nip\uff1a" + this.ip + "\nhostname\uff1a" + this.hostname + "\n\u673a\u5668\u5730\u5740\uff1a" + this.mac + "\n\u89e6\u53d1\u65f6\u95f4\uff1a" + this.time + "\n\u8bf7\u6c42\u5730\u5740\uff1a" + this.requestUri + "\n\u7ebf\u7a0bid\uff1a" + this.threadId + "\n\u6570\u91cf\uff1a" + this.number + "\n\u5806\u6808\uff1a" + this.stack;
    }
}

