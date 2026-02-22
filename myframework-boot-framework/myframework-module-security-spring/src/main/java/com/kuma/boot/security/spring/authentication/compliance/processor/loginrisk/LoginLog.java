/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

import java.util.Date;

public class LoginLog {
    private Integer id;
    private String account;
    private Integer result;
    private String cityCode;
    private String ip;
    private Date time;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getResult() {
        return this.result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

