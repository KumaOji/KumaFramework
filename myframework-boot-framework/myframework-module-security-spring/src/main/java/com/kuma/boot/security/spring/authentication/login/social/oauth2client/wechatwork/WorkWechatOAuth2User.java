/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAlias
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.wechatwork;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.kuma.boot.security.spring.utils.AuthorityUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class WorkWechatOAuth2User
implements OAuth2User {
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String nameAttributeKey;
    private Integer errcode;
    private String errmsg;
    @JsonAlias(value={"OpenId"})
    private String openId;
    @JsonAlias(value={"UserId"})
    private String userId;

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public String getName() {
        return Optional.ofNullable(this.userId).orElse(this.openId);
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

    public Integer getErrcode() {
        return this.errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return this.errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

