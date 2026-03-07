package com.kuma.cloud.blog.security;

import com.kuma.boot.security.spring.access.expression.RoleConstants;
import com.kuma.cloud.blog.domain.vo.LoginResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlogUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final LoginResponse loginResponse;

    public BlogUserDetails(LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
    }

    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleConstants.USER));
        if (loginResponse.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority(RoleConstants.ADMIN));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return loginResponse.getUsername();
    }
}
