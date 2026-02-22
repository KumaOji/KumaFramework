/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

public class RiskRule {
    private Integer id;
    private String riskName;
    private String acceptIp;
    private Integer triggerNumber;
    private Integer triggerTime;
    private Integer triggerTimeType;
    private String unusualLoginTime;
    private Integer operate;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRiskName() {
        return this.riskName;
    }

    public void setRiskName(String riskName) {
        this.riskName = riskName;
    }

    public String getAcceptIp() {
        return this.acceptIp;
    }

    public void setAcceptIp(String acceptIp) {
        this.acceptIp = acceptIp;
    }

    public Integer getTriggerNumber() {
        return this.triggerNumber;
    }

    public void setTriggerNumber(Integer triggerNumber) {
        this.triggerNumber = triggerNumber;
    }

    public Integer getTriggerTime() {
        return this.triggerTime;
    }

    public void setTriggerTime(Integer triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Integer getTriggerTimeType() {
        return this.triggerTimeType;
    }

    public void setTriggerTimeType(Integer triggerTimeType) {
        this.triggerTimeType = triggerTimeType;
    }

    public String getUnusualLoginTime() {
        return this.unusualLoginTime;
    }

    public void setUnusualLoginTime(String unusualLoginTime) {
        this.unusualLoginTime = unusualLoginTime;
    }

    public Integer getOperate() {
        return this.operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }
}

