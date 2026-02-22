/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.core.CredentialsContainer
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.core.authority.AuthorityUtils
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.userdetails;

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
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class MfaUserDetails
implements UserDetails,
CredentialsContainer {
    private final String username;
    private String password;
    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final boolean enableMfa;
    private String secret;

    public MfaUserDetails(String username, String password, boolean enableMfa, String secret, Collection<? extends GrantedAuthority> authorities) {
        this(username, password, enableMfa, secret, true, true, true, true, authorities);
    }

    public MfaUserDetails(String username, String password, boolean enableMfa, String secret, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        Assert.isTrue((username != null && !"".equals(username) && password != null ? 1 : 0) != 0, (String)"Cannot pass null or empty values to constructor");
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableSet(MfaUserDetails.sortAuthorities(authorities));
        this.enableMfa = enableMfa;
        this.secret = secret;
    }

    public void eraseCredentials() {
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
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

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getSecret() {
        return this.secret;
    }

    public boolean isEnableMfa() {
        return this.enableMfa;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public static UserBuilder withUsername(String username) {
        return MfaUserDetails.builder().username(username);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    private static class AuthorityComparator
    implements Comparator<GrantedAuthority>,
    Serializable {
        private AuthorityComparator() {
        }

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }
            return g1.getAuthority() == null ? 1 : g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

    public static final class UserBuilder {
        private String username;
        private String password;
        private String secret;
        private List<GrantedAuthority> authorities;
        private boolean accountExpired;
        private boolean accountLocked;
        private boolean credentialsExpired;
        private boolean disabled;
        private boolean disableMfa;
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

        public UserBuilder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public UserBuilder passwordEncoder(Function<String, String> encoder) {
            Assert.notNull(encoder, (String)"encoder cannot be null");
            this.passwordEncoder = encoder;
            return this;
        }

        public UserBuilder roles(String ... roles) {
            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(roles.length);
            String[] var3 = roles;
            int var4 = roles.length;
            for (int var5 = 0; var5 < var4; ++var5) {
                String role = var3[var5];
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

        public UserBuilder disableMfa(boolean disableMfa) {
            this.disableMfa = disableMfa;
            return this;
        }

        public UserDetails build() {
            String encodedPassword = this.passwordEncoder.apply(this.password);
            return new MfaUserDetails(this.username, encodedPassword, !this.disableMfa, this.secret, !this.disabled, !this.accountExpired, !this.credentialsExpired, !this.accountLocked, this.authorities);
        }
    }
}

