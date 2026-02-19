/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.web.gracefulresponse;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kuma.boot.web.graceful-response")
public class GracefulResponseProperties {
    private boolean printExceptionInGlobalAdvice = false;
    private String responseClassFullName;
    private Integer responseStyle;
    private String defaultSuccessCode = "0";
    private String defaultSuccessMsg = "ok";
    private String defaultErrorCode = "1";
    private String defaultErrorMsg = "error";
    private String defaultValidateErrorCode = "1";
    private List<String> excludePackages;
    private Boolean originExceptionUsingDetailMessage = false;

    public boolean isPrintExceptionInGlobalAdvice() {
        return this.printExceptionInGlobalAdvice;
    }

    public void setPrintExceptionInGlobalAdvice(boolean printExceptionInGlobalAdvice) {
        this.printExceptionInGlobalAdvice = printExceptionInGlobalAdvice;
    }

    public String getDefaultSuccessCode() {
        return this.defaultSuccessCode;
    }

    public void setDefaultSuccessCode(String defaultSuccessCode) {
        this.defaultSuccessCode = defaultSuccessCode;
    }

    public String getDefaultSuccessMsg() {
        return this.defaultSuccessMsg;
    }

    public void setDefaultSuccessMsg(String defaultSuccessMsg) {
        this.defaultSuccessMsg = defaultSuccessMsg;
    }

    public String getDefaultErrorCode() {
        return this.defaultErrorCode;
    }

    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    public String getDefaultErrorMsg() {
        return this.defaultErrorMsg;
    }

    public void setDefaultErrorMsg(String defaultErrorMsg) {
        this.defaultErrorMsg = defaultErrorMsg;
    }

    public String getResponseClassFullName() {
        return this.responseClassFullName;
    }

    public void setResponseClassFullName(String responseClassFullName) {
        this.responseClassFullName = responseClassFullName;
    }

    public Integer getResponseStyle() {
        return this.responseStyle;
    }

    public void setResponseStyle(Integer responseStyle) {
        this.responseStyle = responseStyle;
    }

    public String getDefaultValidateErrorCode() {
        return this.defaultValidateErrorCode;
    }

    public void setDefaultValidateErrorCode(String defaultValidateErrorCode) {
        this.defaultValidateErrorCode = defaultValidateErrorCode;
    }

    public List<String> getExcludePackages() {
        return this.excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    public Boolean getOriginExceptionUsingDetailMessage() {
        return this.originExceptionUsingDetailMessage;
    }

    public void setOriginExceptionUsingDetailMessage(Boolean originExceptionUsingDetailMessage) {
        this.originExceptionUsingDetailMessage = originExceptionUsingDetailMessage;
    }
}

