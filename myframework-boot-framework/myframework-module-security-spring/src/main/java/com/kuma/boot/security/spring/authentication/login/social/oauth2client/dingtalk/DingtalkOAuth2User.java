/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.dingtalk;

import com.kuma.boot.security.spring.utils.AuthorityUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class DingtalkOAuth2User
implements OAuth2User,
Serializable {
    private static final long serialVersionUID = 1L;
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String nameAttributeKey;
    private String accessToken;
    private String corpId;
    private Long expireIn;
    private String refreshToken;
    private String avatarUrl;
    private String email;
    private String mobile;
    private String nick;
    private String openId;
    private String stateCode;
    private String unionId;

    public String getName() {
        return this.nameAttributeKey;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCorpId() {
        return this.corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public Long getExpireIn() {
        return this.expireIn;
    }

    public void setExpireIn(Long expireIn) {
        this.expireIn = expireIn;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getStateCode() {
        return this.stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}

