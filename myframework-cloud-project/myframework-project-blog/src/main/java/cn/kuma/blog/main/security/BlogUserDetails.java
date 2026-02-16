package cn.kuma.blog.main.security;

import cn.kuma.blog.common.model.domain.UserDetail;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 适配 Spring Security UserDetails，包装现有 UserDetail，用于 SecurityContext。
 *
 * @author Kuma
 * @version 1.0
 */
@Getter
public class BlogUserDetails implements UserDetails {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserDetail userDetail;

    public BlogUserDetails(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userDetail == null) {
            return Collections.emptyList();
        }
        if (userDetail.isAdminGrade()) {
            return Stream.of(ROLE_USER, ROLE_ADMIN)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userDetail != null ? userDetail.getUserID() : null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
