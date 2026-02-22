/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.core.user.OAuth2User
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

public class QQOAuth2User
implements OAuth2User {
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String nameAttributeKey;
    private String nickname;
    @JsonProperty(value="figureurl")
    private String figureUrl30;
    @JsonProperty(value="figureurl_1")
    private String figureUrl50;
    @JsonProperty(value="figureurl_2")
    private String figureUrl100;
    @JsonProperty(value="figureurl_qq_1")
    private String qqFigureUrl40;
    @JsonProperty(value="figureurl_qq_2")
    private String qqFigureUrl100;
    private String gender;
    private String openId;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

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
        return this.nameAttributeKey;
    }

    public void setNameAttributeKey(String nameAttributeKey) {
        this.nameAttributeKey = nameAttributeKey;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFigureUrl30() {
        return this.figureUrl30;
    }

    public void setFigureUrl30(String figureUrl30) {
        this.figureUrl30 = figureUrl30;
    }

    public String getFigureUrl50() {
        return this.figureUrl50;
    }

    public void setFigureUrl50(String figureUrl50) {
        this.figureUrl50 = figureUrl50;
    }

    public String getFigureUrl100() {
        return this.figureUrl100;
    }

    public void setFigureUrl100(String figureUrl100) {
        this.figureUrl100 = figureUrl100;
    }

    public String getQqFigureUrl40() {
        return this.qqFigureUrl40;
    }

    public void setQqFigureUrl40(String qqFigureUrl40) {
        this.qqFigureUrl40 = qqFigureUrl40;
    }

    public String getQqFigureUrl100() {
        return this.qqFigureUrl100;
    }

    public void setQqFigureUrl100(String qqFigureUrl100) {
        this.qqFigureUrl100 = qqFigureUrl100;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}

