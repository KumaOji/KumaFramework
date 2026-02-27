/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.captcha.autoconfigure.properties;

import com.kuma.boot.captcha.model.CaptchaTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="kuma.boot.captcha")
public class CaptchaProperties {
    public static final String PREFIX = "kuma.boot.captcha";
    private boolean enabled = false;
    private CaptchaTypeEnum type = CaptchaTypeEnum.DEFAULT;
    private String jigsaw = "";
    private String picClick = "";
    private String waterMark = "\u6211\u7684\u6c34\u5370";
    private String waterFont = "WenQuanZhengHei.ttf";
    private String fontType = "WenQuanZhengHei.ttf";
    private String slipOffset = "5";
    private Boolean aesStatus = true;
    private String interferenceOptions = "0";
    private String cacheNumber = "1000";
    private String timingClear = "180";
    private StorageType cacheType = StorageType.redis;
    private boolean historyDataClearEnable = false;
    private boolean reqFrequencyLimitEnable = true;
    private int reqGetLockLimit = 5;
    private int reqGetLockSeconds = 300;
    private int reqGetMinuteLimit = 100;
    private int reqCheckMinuteLimit = 100;
    private int reqVerifyMinuteLimit = 100;

    public boolean getHistoryDataClearEnable() {
        return this.historyDataClearEnable;
    }

    public void setHistoryDataClearEnable(boolean historyDataClearEnable) {
        this.historyDataClearEnable = historyDataClearEnable;
    }

    public boolean getReqFrequencyLimitEnable() {
        return this.reqFrequencyLimitEnable;
    }

    public void setReqFrequencyLimitEnable(boolean reqFrequencyLimitEnable) {
        this.reqFrequencyLimitEnable = reqFrequencyLimitEnable;
    }

    public int getReqGetLockLimit() {
        return this.reqGetLockLimit;
    }

    public void setReqGetLockLimit(int reqGetLockLimit) {
        this.reqGetLockLimit = reqGetLockLimit;
    }

    public int getReqGetLockSeconds() {
        return this.reqGetLockSeconds;
    }

    public void setReqGetLockSeconds(int reqGetLockSeconds) {
        this.reqGetLockSeconds = reqGetLockSeconds;
    }

    public int getReqGetMinuteLimit() {
        return this.reqGetMinuteLimit;
    }

    public void setReqGetMinuteLimit(int reqGetMinuteLimit) {
        this.reqGetMinuteLimit = reqGetMinuteLimit;
    }

    public int getReqCheckMinuteLimit() {
        return this.reqCheckMinuteLimit;
    }

    public void setReqCheckMinuteLimit(int reqCheckMinuteLimit) {
        this.reqCheckMinuteLimit = reqCheckMinuteLimit;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isHistoryDataClearEnable() {
        return this.historyDataClearEnable;
    }

    public boolean isReqFrequencyLimitEnable() {
        return this.reqFrequencyLimitEnable;
    }

    public int getReqVerifyMinuteLimit() {
        return this.reqVerifyMinuteLimit;
    }

    public void setReqVerifyMinuteLimit(int reqVerifyMinuteLimit) {
        this.reqVerifyMinuteLimit = reqVerifyMinuteLimit;
    }

    public static String getPREFIX() {
        return PREFIX;
    }

    public CaptchaTypeEnum getType() {
        return this.type;
    }

    public void setType(CaptchaTypeEnum type) {
        this.type = type;
    }

    public String getJigsaw() {
        return this.jigsaw;
    }

    public void setJigsaw(String jigsaw) {
        this.jigsaw = jigsaw;
    }

    public String getPicClick() {
        return this.picClick;
    }

    public void setPicClick(String picClick) {
        this.picClick = picClick;
    }

    public String getWaterMark() {
        return this.waterMark;
    }

    public void setWaterMark(String waterMark) {
        this.waterMark = waterMark;
    }

    public String getWaterFont() {
        return this.waterFont;
    }

    public void setWaterFont(String waterFont) {
        this.waterFont = waterFont;
    }

    public String getFontType() {
        return this.fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public String getSlipOffset() {
        return this.slipOffset;
    }

    public void setSlipOffset(String slipOffset) {
        this.slipOffset = slipOffset;
    }

    public Boolean getAesStatus() {
        return this.aesStatus;
    }

    public void setAesStatus(Boolean aesStatus) {
        this.aesStatus = aesStatus;
    }

    public StorageType getCacheType() {
        return this.cacheType;
    }

    public void setCacheType(StorageType cacheType) {
        this.cacheType = cacheType;
    }

    public String getInterferenceOptions() {
        return this.interferenceOptions;
    }

    public void setInterferenceOptions(String interferenceOptions) {
        this.interferenceOptions = interferenceOptions;
    }

    public String getCacheNumber() {
        return this.cacheNumber;
    }

    public void setCacheNumber(String cacheNumber) {
        this.cacheNumber = cacheNumber;
    }

    public String getTimingClear() {
        return this.timingClear;
    }

    public void setTimingClear(String timingClear) {
        this.timingClear = timingClear;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static enum StorageType {
        local,
        redis,
        other;

    }
}

