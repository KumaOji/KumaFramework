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

package com.kuma.boot.security.spring.core.userdetails;

import cn.hutool.core.collection.CollUtil;
import tools.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.security.spring.core.authority.KmcGrantedAuthority;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * 希罗多德用户
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:03:42
 */
@JsonDeserialize(using = KmcUserDeserializer.class)
public class KmcUser extends BaseSecurityUser implements UserDetails, CredentialsContainer {

    @Serial
    private static final long serialVersionUID = 6610083281801529147L;

    /**
     * 当局
     */
    private Set<GrantedAuthority> authorities;

    public KmcUser() {}

    public KmcUser(
            Long userId,
            String username,
            String password,
            Set<String> permissions,
            Set<String> roleCodes) {
        super(userId, username, password, true, true, true, true, permissions, roleCodes);
        this.authorities = Collections.unmodifiableSet(sortAuthorities(buildAuthorities()));
    }

    /**
     * Calls the more complex constructor with all boolean arguments set to {@code true}.
     *
     * @param userId      用户id
     * @param username    用户名
     * @param password    密码
     * @param authorities 当局
     * @since 2023-07-04 10:03:42
     */
    public KmcUser(
            Long userId,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {
        this(userId, username, password, true, true, true, true, authorities);
    }

    /**
     * Construct the <code>User</code> with the details required by
     * <code>org.springframework.security.authentication.dao.DaoAuthenticationProvider</code>
     *
     * @param userId                用户id
     * @param username              the username presented to the
     * @param password              the password that should be presented to the
     * @param enabled               set to <code>true</code> if the user is enabled
     * @param accountNonExpired     set to <code>true</code> if the account has not expired
     * @param credentialsNonExpired set to <code>true</code> if the credentials have not
     * @param accountNonLocked      set to <code>true</code> if the account is not locked
     * @since 2023-07-04 10:03:42
     */
    public KmcUser(
            Long userId,
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Set<String> permissions,
            Set<String> roleCodes) {
        super(
                userId,
                username,
                password,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                permissions,
                roleCodes);

        this.authorities = Collections.unmodifiableSet(sortAuthorities(buildAuthorities()));
    }

    public KmcUser(
            Long userId,
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(
                userId,
                username,
                password,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                new HashSet<>(),
                new HashSet<>());

        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }

    /**
     * 得到当局
     *
     * @return {@link Collection }<{@link GrantedAuthority }>
     * @since 2023-07-04 10:03:42
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * 启用了
     *
     * @return boolean
     * @since 2023-07-04 10:03:43
     */
    @Override
    public boolean isEnabled() {
        return super.getEnabled();
    }

    /**
     * 是账户非过期
     *
     * @return boolean
     * @since 2023-07-04 10:03:43
     */
    @Override
    public boolean isAccountNonExpired() {
        return super.getAccountNonExpired();
    }

    /**
     * 是账户非锁定
     *
     * @return boolean
     * @since 2023-07-04 10:03:43
     */
    @Override
    public boolean isAccountNonLocked() {
        return super.getAccountNonLocked();
    }

    /**
     * 是凭证不过期
     *
     * @return boolean
     * @since 2023-07-04 10:03:43
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return super.getCredentialsNonExpired();
    }

    /**
     * 删除凭证
     *
     * @since 2023-07-04 10:03:43
     */
    @Override
    public void eraseCredentials() {
        super.setPassword(null);
    }

    /**
     * 那种当局
     *
     * @param authorities 当局
     * @return {@link SortedSet }<{@link GrantedAuthority }>
     * @since 2023-07-04 10:03:44
     */
    private static SortedSet<GrantedAuthority> sortAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(
                    grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    public Collection<? extends GrantedAuthority> buildAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        if (!CollUtil.isEmpty(super.getRoleCodes())) {
            super.getRoleCodes().parallelStream()
                    .forEach(role -> authorities.add(new KmcGrantedAuthority(ROLE_PREFIX + role)));
        }

        if (!CollUtil.isEmpty(super.getPermissions())) {
            super.getPermissions().parallelStream()
                    .forEach(permission -> authorities.add(new KmcGrantedAuthority(permission)));
        }
        return authorities;
    }

    /**
     * 权威比较器
     *
     * @author kuma
     * @version 2023.07
     * @since 2023-07-04 10:03:45
     */
    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

        /**
         * 串行版本uid
         */
        @Serial
        private static final long serialVersionUID = 6610083281801529147L;

        /**
         * 比较
         *
         * @param g1 g1
         * @param g2 g2
         * @return int
         * @since 2023-07-04 10:03:45
         */
        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set. If the authority is null, it is a custom authority and should
            // precede others.
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

    /**
     * Returns {@code true} if the supplied object is a {@code User} instance with the same
     * {@code username} value.
     * <p>
     * In other words, the objects are equal if they have the same username, representing the same
     * principal.
     *
     * @param o o
     * @return boolean
     * @since 2023-07-04 10:03:44
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KmcUser that = (KmcUser) o;
        return Objects.equal(super.getUserId(), that.getUserId())
                && Objects.equal(super.getUsername(), that.getUsername());
    }

    /**
     * Returns the hashcode of the {@code username}.
     *
     * @return int
     * @since 2023-07-04 10:03:44
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(super.getUserId(), super.getUsername());
    }

    /**
     * 字符串
     *
     * @return {@link String }
     * @since 2023-07-04 10:03:44
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", super.getUserId())
                .add("password", "[PROTECTED]")
                .add("account", super.getAccount())
                .add("nickname", super.getNickname())
                .add("username", super.getUsername())
                .add("phone", super.getPhone())
                .add("mobile", super.getMobile())
                .add("email", super.getEmail())
                .add("sex", super.getSex())
                .add("birthday", super.getBirthday())
                .add("avatar", super.getAvatar())
                .add("status", super.getStatus())
                .add("lockFlag", super.getLockFlag())
                .add("delFlag", super.getDelFlag())
                .add("type", super.getType())
                .add("orgIds", super.getOrgIds())
                .add("deptIds", super.getDeptIds())
                .add("positionIds", super.getPositionIds())
                .add("roleIds", super.getRoleIds())
                .add("roleCodes", super.getRoleCodes())
                .add("permissions", super.getPermissions())
                .add("dataScopes", super.getDataScopes())
                .add("tenant", super.getTenant())
                .add("tenantSuperAdmin", super.isTenantSuperAdmin())
                .add("tenantAdmin", super.isTenantAdmin())
                .add("admin", super.getAdmin())
                .add("storeId", super.getStoreId())
                .add("accountNonExpired", super.getAccountNonExpired())
                .add("accountNonLocked", super.getAccountNonLocked())
                .add("credentialsNonExpired", super.getCredentialsNonExpired())
                .add("enabled", super.getEnabled())
                .toString();
    }
}
