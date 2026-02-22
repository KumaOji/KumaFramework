/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.core.authority;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class TtcGrantedAuthority
implements GrantedAuthority {
    private String authority;

    public TtcGrantedAuthority(String authority) {
        Assert.hasText((String)authority, (String)"A granted authority textual representation is required");
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TtcGrantedAuthority that = (TtcGrantedAuthority)o;
        return Objects.equal((Object)this.authority, (Object)that.authority);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.authority});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("authority", (Object)this.authority).toString();
    }
}

