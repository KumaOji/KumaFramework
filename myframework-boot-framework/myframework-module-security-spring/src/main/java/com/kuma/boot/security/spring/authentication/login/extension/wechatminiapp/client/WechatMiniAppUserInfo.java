/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.client;

public class WechatMiniAppUserInfo {
    public WechatMiniAppUserInfo() {}

    public WechatMiniAppUserInfo(
            String openId,
            String sessionKey,
            String avatarUrl,
            String city,
            String country,
            String gender,
            String language,
            String nickName,
            String realName,
            String province,
            Integer stuId) {
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

    // OPEN_id
    private String openId;
    // 会话密钥
    private String sessionKey;
    // 头像路径
    private String avatarUrl;
    // 城市
    private String city;
    // 国家
    private String country;
    // 性别
    private String gender;
    // 语言
    private String language;
    // 昵称
    private String nickName;
    // 备注名或真实名
    private String realName;
    // 省份
    private String province;
    private Integer stuId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getStuId() {
        return stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }
}
