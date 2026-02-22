/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.kuma.boot.common.utils.log.LogUtils
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.security.core.CredentialsContainer
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.core.authority.AuthorityUtils
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.crypto.factory.PasswordEncoderFactories
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes.TemporaryUserDeserializer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
@JsonDeserialize(using=TemporaryUserDeserializer.class)
public class TemporaryUser
implements UserDetails,
CredentialsContainer {
    private static final long serialVersionUID = 620L;
    private String password;
    private final String username;
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final AuthUser authUser;
    private final String encodeState;

    public TemporaryUser(String username, String password, Collection<? extends GrantedAuthority> authorities, AuthUser authUser, String encodeState) {
        this(username, password, true, true, true, true, authorities, authUser, encodeState);
    }

    public TemporaryUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, AuthUser authUser, String encodeState) {
        if (username == null || "".equals(username) || password == null) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableSet(TemporaryUser.sortAuthorities(authorities));
        this.authUser = authUser;
        this.encodeState = encodeState;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public AuthUser getAuthUser() {
        return this.authUser;
    }

    public String getEncodeState() {
        return this.encodeState;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public void eraseCredentials() {
        this.password = null;
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

    public boolean equals(Object rhs) {
        if (rhs instanceof TemporaryUser) {
            return this.username.equals(((TemporaryUser)rhs).username);
        }
        return false;
    }

    public int hashCode() {
        return this.username.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");
        sb.append("authUser: ").append(this.authUser.toString()).append("; ");
        sb.append("encodeState: ").append(this.encodeState).append("; ");
        if (!this.authorities.isEmpty()) {
            sb.append("Granted Authorities: ");
            boolean first = true;
            for (GrantedAuthority auth : this.authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }
        return sb.toString();
    }

    public static UserBuilder withUsername(String username) {
        return TemporaryUser.builder().username(username);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    @Deprecated
    public static UserBuilder withDefaultPasswordEncoder() {
        LogUtils.warn((String)"User.withDefaultPasswordEncoder() is considered unsafe for production and is only intended for sample applications.", (Object[])new Object[0]);
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return TemporaryUser.builder().passwordEncoder(arg_0 -> ((PasswordEncoder)encoder).encode(arg_0));
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

    public static class UserBuilder {
        private String username;
        private String password;
        private List<GrantedAuthority> authorities;
        private AuthUser authUser;
        private String encodeState;
        private boolean accountExpired;
        private boolean accountLocked;
        private boolean credentialsExpired;
        private boolean disabled;
        private Function<String, String> passwordEncoder = password -> password;

        private UserBuilder() {
        }

        public UserBuilder username(String username) {
            Assert.notNull((Object)username, (String)"username cannot be null");
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            Assert.notNull((Object)password, (String)"password cannot be null");
            this.password = password;
            return this;
        }

        public UserBuilder passwordEncoder(Function<String, String> encoder) {
            Assert.notNull(encoder, (String)"encoder cannot be null");
            this.passwordEncoder = encoder;
            return this;
        }

        public UserBuilder roles(String ... roles) {
            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(roles.length);
            for (String role : roles) {
                Assert.isTrue((!role.startsWith("ROLE_") ? 1 : 0) != 0, () -> role + " cannot start with ROLE_ (it is automatically added)");
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return this.authorities(authorities);
        }

        public UserBuilder authorities(GrantedAuthority ... authorities) {
            return this.authorities(Arrays.asList(authorities));
        }

        public UserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<GrantedAuthority>(authorities);
            return this;
        }

        public UserBuilder authorities(String ... authorities) {
            return this.authorities(AuthorityUtils.createAuthorityList((String[])authorities));
        }

        public UserBuilder authUser(AuthUser authUser) {
            this.authUser = authUser;
            return this;
        }

        public UserBuilder encodeState(String encodeState) {
            this.encodeState = encodeState;
            return this;
        }

        public UserBuilder accountExpired(boolean accountExpired) {
            this.accountExpired = accountExpired;
            return this;
        }

        public UserBuilder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public UserBuilder credentialsExpired(boolean credentialsExpired) {
            this.credentialsExpired = credentialsExpired;
            return this;
        }

        public UserBuilder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public UserDetails build() {
            String encodedPassword = this.passwordEncoder.apply(this.password);
            return new TemporaryUser(this.username, encodedPassword, !this.disabled, !this.accountExpired, !this.credentialsExpired, !this.accountLocked, this.authorities, this.authUser, this.encodeState);
        }
    }
}

