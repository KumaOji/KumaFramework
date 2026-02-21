/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.http;

import java.io.Serializable;

public class IpAnalyzeBaiduResponse
implements Serializable {
    private Integer status;
    private String message;
    private String address;
    private IpAnalyzeContent content;

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public IpAnalyzeContent getContent() {
        return this.content;
    }

    public void setContent(IpAnalyzeContent content) {
        this.content = content;
    }
}

