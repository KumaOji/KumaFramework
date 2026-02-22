/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client;

public class WechatMiniAppUserInfo {
    private String openId;
    private String sessionKey;
    private String avatarUrl;
    private String city;
    private String country;
    private String gender;
    private String language;
    private String nickName;
    private String realName;
    private String province;
    private Integer stuId;

    public WechatMiniAppUserInfo() {
    }

    public WechatMiniAppUserInfo(String openId, String sessionKey, String avatarUrl, String city, String country, String gender, String language, String nickName, String realName, String province, Integer stuId) {
        this.openId = openId;
        this.sessionKey = sessionKey;
        this.avatarUrl = avatarUrl;
        this.city = city;
        this.country = country;
        this.gender = gender;
        this.language = language;
        this.nickName = nickName;
        this.realName = realName;
        this.province = province;
        this.stuId = stuId;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return this.realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getStuId() {
        return this.stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }
}

