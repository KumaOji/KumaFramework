/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 */
package com.kuma.boot.security.spring.event.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class TtcUserStatus {
    private String userId;
    private String status;

    public TtcUserStatus() {
    }

    public TtcUserStatus(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TtcUserStatus that = (TtcUserStatus)o;
        return Objects.equal((Object)this.userId, (Object)that.userId) && Objects.equal((Object)this.status, (Object)that.status);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.userId, this.status});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("userId", (Object)this.userId).add("status", (Object)this.status).toString();
    }
}

