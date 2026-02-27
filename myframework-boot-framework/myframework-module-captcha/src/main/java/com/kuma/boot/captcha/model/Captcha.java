/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.model;

import java.io.Serializable;
import java.util.List;

public class Captcha
implements Serializable {
    private String captchaId;
    private String projectCode;
    private String captchaType;
    private String captchaOriginalPath;
    private String captchaFontType;
    private Integer captchaFontSize;
    private String secretKey;
    private String originalImageBase64;
    private Point point;
    private String jigsawImageBase64;
    private List<String> wordList;
    private List<java.awt.Point> pointList;
    private String pointJson;
    private String token;
    private Boolean result = false;
    private String captchaVerification;
    private String clientUid;
    private Long ts;
    private String browserInfo;

    public void resetClientFlag() {
        this.browserInfo = null;
        this.clientUid = null;
    }

    public String getCaptchaId() {
        return this.captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getProjectCode() {
        return this.projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getCaptchaType() {
        return this.captchaType;
    }

    public void setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
    }

    public String getCaptchaOriginalPath() {
        return this.captchaOriginalPath;
    }

    public void setCaptchaOriginalPath(String captchaOriginalPath) {
        this.captchaOriginalPath = captchaOriginalPath;
    }

    public String getCaptchaFontType() {
        return this.captchaFontType;
    }

    public void setCaptchaFontType(String captchaFontType) {
        this.captchaFontType = captchaFontType;
    }

    public Integer getCaptchaFontSize() {
        return this.captchaFontSize;
    }

    public void setCaptchaFontSize(Integer captchaFontSize) {
        this.captchaFontSize = captchaFontSize;
    }

    public String getOriginalImageBase64() {
        return this.originalImageBase64;
    }

    public void setOriginalImageBase64(String originalImageBase64) {
        this.originalImageBase64 = originalImageBase64;
    }

    public Point getPoint() {
        return this.point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getJigsawImageBase64() {
        return this.jigsawImageBase64;
    }

    public void setJigsawImageBase64(String jigsawImageBase64) {
        this.jigsawImageBase64 = jigsawImageBase64;
    }

    public List<String> getWordList() {
        return this.wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public List<java.awt.Point> getPointList() {
        return this.pointList;
    }

    public void setPointList(List<java.awt.Point> pointList) {
        this.pointList = pointList;
    }

    public String getPointJson() {
        return this.pointJson;
    }

    public void setPointJson(String pointJson) {
        this.pointJson = pointJson;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getResult() {
        return this.result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getCaptchaVerification() {
        return this.captchaVerification;
    }

    public void setCaptchaVerification(String captchaVerification) {
        this.captchaVerification = captchaVerification;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getClientUid() {
        return this.clientUid;
    }

    public void setClientUid(String clientUid) {
        this.clientUid = clientUid;
    }

    public Long getTs() {
        return this.ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getBrowserInfo() {
        return this.browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }
}

