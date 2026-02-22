/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 */
package com.kuma.boot.security.spring.core;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PrincipalDetails {
    private String openId;
    private String userName;
    private Set<String> roles;
    private String employeeId;
    private String avatar;

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("openid", this.openId);
        map.put("username", this.userName);
        map.put("roles", this.roles);
        map.put("employeeId", this.employeeId);
        map.put("avatar", this.avatar);
        return map;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PrincipalDetails that = (PrincipalDetails)o;
        return Objects.equal((Object)this.openId, (Object)that.openId);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.openId});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("userId", (Object)this.openId).add("userName", (Object)this.userName).add("roles", this.roles).add("employeeId", (Object)this.employeeId).add("avatar", (Object)this.avatar).toString();
    }
}

