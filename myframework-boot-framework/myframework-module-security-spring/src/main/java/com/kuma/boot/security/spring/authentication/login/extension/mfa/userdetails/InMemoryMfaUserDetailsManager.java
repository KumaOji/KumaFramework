/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.security.access.AccessDeniedException
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UserDetailsPasswordService
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 *  org.springframework.security.provisioning.UserDetailsManager
 *  org.springframework.util.Assert
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.userdetails;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

public class InMemoryMfaUserDetailsManager
implements UserDetailsManager,
UserDetailsPasswordService {
    private final Map<String, UserDetails> users = new HashMap<String, UserDetails>();
    private AuthenticationManager authenticationManager;

    public InMemoryMfaUserDetailsManager() {
    }

    public InMemoryMfaUserDetailsManager(UserDetails ... users) {
        UserDetails[] userDetails = users;
        int length = users.length;
        for (int i = 0; i < length; ++i) {
            UserDetails user = userDetails[i];
            this.createUser(user);
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MfaUserDetails user = (MfaUserDetails)this.users.get(username.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MfaUserDetails(user.getUsername(), user.getPassword(), user.isEnableMfa(), user.getSecret(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
    }

    public void createUser(UserDetails user) {
        Assert.isTrue((!this.userExists(user.getUsername()) ? 1 : 0) != 0, (String)"user should not exist");
        this.users.put(user.getUsername().toLowerCase(), user);
    }

    public void updateUser(UserDetails user) {
        Assert.isTrue((boolean)this.userExists(user.getUsername()), (String)"user should exist");
        this.users.put(user.getUsername().toLowerCase(), user);
    }

    public void deleteUser(String username) {
        this.users.remove(username.toLowerCase());
    }

    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        }
        String username = currentUser.getName();
        LogUtils.debug((String)String.format("Changing password for user '%s'", username), (Object[])new Object[0]);
        if (this.authenticationManager != null) {
            LogUtils.debug((String)String.format("Reauthenticating user '%s' for password change request.", username), (Object[])new Object[0]);
            this.authenticationManager.authenticate((Authentication)UsernamePasswordAuthenticationToken.unauthenticated((Object)username, (Object)oldPassword));
        } else {
            LogUtils.debug((String)"No authentication manager set. Password won't be re-checked.", (Object[])new Object[0]);
        }
        MfaUserDetails user = (MfaUserDetails)this.users.get(username);
        Assert.state((user != null ? 1 : 0) != 0, (String)"Current user doesn't exist in database.");
        user.setPassword(newPassword);
    }

    public boolean userExists(String username) {
        return this.users.containsKey(username.toLowerCase());
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public UserDetails updatePassword(UserDetails user, String newPassword) {
        String username = user.getUsername();
        MfaUserDetails mfaUserDetails = (MfaUserDetails)this.users.get(username.toLowerCase());
        mfaUserDetails.setPassword(newPassword);
        return mfaUserDetails;
    }
}

