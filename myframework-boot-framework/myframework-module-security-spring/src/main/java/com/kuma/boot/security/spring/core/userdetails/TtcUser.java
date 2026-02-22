/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  com.kuma.boot.common.model.BaseSecurityUser
 *  org.dromara.hutool.core.collection.CollUtil
 *  org.springframework.security.core.CredentialsContainer
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.core.userdetails;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.security.spring.core.authority.TtcGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@JsonDeserialize(using=TtcUserDeserializer.class)
public class TtcUser
extends BaseSecurityUser
implements UserDetails,
CredentialsContainer {
    private static final long serialVersionUID = 620L;
    private Set<GrantedAuthority> authorities;

    public TtcUser() {
    }

    public TtcUser(Long userId, String username, String password, Set<String> permissions, Set<String> roleCodes) {
        super(userId, username, password, true, true, true, true, permissions, roleCodes);
        this.authorities = Collections.unmodifiableSet(TtcUser.sortAuthorities(this.buildAuthorities()));
    }

    public TtcUser(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(userId, username, password, true, true, true, true, authorities);
    }

    public TtcUser(Long userId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Set<String> permissions, Set<String> roleCodes) {
        super(userId, username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, permissions, roleCodes);
        this.authorities = Collections.unmodifiableSet(TtcUser.sortAuthorities(this.buildAuthorities()));
    }

    public TtcUser(Long userId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(userId, username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, new HashSet(), new HashSet());
        this.authorities = Collections.unmodifiableSet(TtcUser.sortAuthorities(authorities));
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public boolean isEnabled() {
        return super.getEnabled();
    }

    public boolean isAccountNonExpired() {
        return super.getAccountNonExpired();
    }

    public boolean isAccountNonLocked() {
        return super.getAccountNonLocked();
    }

    public boolean isCredentialsNonExpired() {
        return super.getCredentialsNonExpired();
    }

    public void eraseCredentials() {
        super.setPassword(null);
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, (String)"Cannot pass a null GrantedAuthority collection");
        TreeSet<GrantedAuthority> sortedAuthorities = new TreeSet<GrantedAuthority>(new AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull((Object)grantedAuthority, (String)"GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    public Collection<? extends GrantedAuthority> buildAuthorities() {
        HashSet authorities = new HashSet();
        if (!CollUtil.isEmpty((Collection)super.getRoleCodes())) {
            super.getRoleCodes().parallelStream().forEach(role -> authorities.add(new TtcGrantedAuthority("ROLE_" + role)));
        }
        if (!CollUtil.isEmpty((Collection)super.getPermissions())) {
            super.getPermissions().parallelStream().forEach(permission -> authorities.add(new TtcGrantedAuthority((String)permission)));
        }
        return authorities;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        TtcUser that = (TtcUser)((Object)o);
        return Objects.equal((Object)super.getUserId(), (Object)that.getUserId()) && Objects.equal((Object)super.getUsername(), (Object)that.getUsername());
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{super.getUserId(), super.getUsername()});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)((Object)this)).add("userId", (Object)super.getUserId()).add("password", (Object)"[PROTECTED]").add("account", (Object)super.getAccount()).add("nickname", (Object)super.getNickname()).add("username", (Object)super.getUsername()).add("phone", (Object)super.getPhone()).add("mobile", (Object)super.getMobile()).add("email", (Object)super.getEmail()).add("sex", (Object)super.getSex()).add("birthday", (Object)super.getBirthday()).add("avatar", (Object)super.getAvatar()).add("status", (Object)super.getStatus()).add("lockFlag", (Object)super.getLockFlag()).add("delFlag", (Object)super.getDelFlag()).add("type", (Object)super.getType()).add("orgIds", (Object)super.getOrgIds()).add("deptIds", (Object)super.getDeptIds()).add("positionIds", (Object)super.getPositionIds()).add("roleIds", (Object)super.getRoleIds()).add("roleCodes", (Object)super.getRoleCodes()).add("permissions", (Object)super.getPermissions()).add("dataScopes", (Object)super.getDataScopes()).add("tenant", (Object)super.getTenant()).add("tenantSuperAdmin", super.isTenantSuperAdmin()).add("tenantAdmin", super.isTenantAdmin()).add("admin", super.getAdmin()).add("storeId", (Object)super.getStoreId()).add("accountNonExpired", super.getAccountNonExpired()).add("accountNonLocked", super.getAccountNonLocked()).add("credentialsNonExpired", super.getCredentialsNonExpired()).add("enabled", super.getEnabled()).toString();
    }

    private static class AuthorityComparator
    implements Comparator<GrantedAuthority>,
    Serializable {
        private static final long serialVersionUID = 620L;

        private AuthorityComparator() {
        }

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }
}

