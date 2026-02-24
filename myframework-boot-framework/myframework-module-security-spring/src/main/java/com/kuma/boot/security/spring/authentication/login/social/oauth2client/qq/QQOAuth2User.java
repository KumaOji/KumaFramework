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

package com.kuma.boot.security.spring.authentication.login.social.oauth2client.qq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kuma.boot.security.spring.utils.AuthorityUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class QQOAuth2User implements OAuth2User {

    // 统一赋予USER角色
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<>();
    private String nameAttributeKey;

    private String nickname;

    @JsonProperty("figureurl")
    private String figureUrl30;

    @JsonProperty("figureurl_1")
    private String figureUrl50;

    @JsonProperty("figureurl_2")
    private String figureUrl100;

    @JsonProperty("figureurl_qq_1")
    private String qqFigureUrl40;

    @JsonProperty("figureurl_qq_2")
    private String qqFigureUrl100;

    private String gender;
    // 携带openId备用
    private String openId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return this.nameAttributeKey;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getNameAttributeKey() {
        return nameAttributeKey;
    }

    public void setNameAttributeKey(String nameAttributeKey) {
        this.nameAttributeKey = nameAttributeKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFigureUrl30() {
        return figureUrl30;
    }

    public void setFigureUrl30(String figureUrl30) {
        this.figureUrl30 = figureUrl30;
    }

    public String getFigureUrl50() {
        return figureUrl50;
    }

    public void setFigureUrl50(String figureUrl50) {
        this.figureUrl50 = figureUrl50;
    }

    public String getFigureUrl100() {
        return figureUrl100;
    }

    public void setFigureUrl100(String figureUrl100) {
        this.figureUrl100 = figureUrl100;
    }

    public String getQqFigureUrl40() {
        return qqFigureUrl40;
    }

    public void setQqFigureUrl40(String qqFigureUrl40) {
        this.qqFigureUrl40 = qqFigureUrl40;
    }

    public String getQqFigureUrl100() {
        return qqFigureUrl100;
    }

    public void setQqFigureUrl100(String qqFigureUrl100) {
        this.qqFigureUrl100 = qqFigureUrl100;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
