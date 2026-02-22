/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.wechat;

import com.kuma.boot.security.spring.utils.AuthorityUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class WechatOAuth2User
implements OAuth2User {
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String nameAttributeKey;
    private String openid;
    private String nickname;
    private Integer sex;
    private String province;
    private String city;
    private String country;
    private String headimgurl;
    private List<String> privilege;
    private String unionid;

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
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

    public String getHeadimgurl() {
        return this.headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public List<String> getPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(List<String> privilege) {
        this.privilege = privilege;
    }

    public String getUnionid() {
        return this.unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}

