package com.kuma.cloud.uaa.security;

import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.service.UaaUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 从 UAA 自有表加载登录主体。
 *
 * <p>刻意返回 Spring 内置的 {@link User} 而非自定义 UserDetails：授权码与刷新令牌流程中，
 * {@code JdbcOAuth2AuthorizationService} 会把认证主体序列化进 oauth2_authorization.attributes，
 * 内置类型已被 Spring Security 的 Jackson 模块支持，自定义类型则需额外注册 Mixin 才能反序列化。
 * 签发 Token 时缺少的业务字段由 {@link UaaJwtTokenCustomizer} 按用户名回查补齐。
 *
 * @author kuma
 */
@Service
@RequiredArgsConstructor
public class UaaUserDetailsService implements UserDetailsService {

    private final UaaUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UaaUser user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(resolveAuthorities(user.getId()).toArray(String[]::new))
                .disabled(!user.isEnabled())
                .accountLocked(user.isLocked())
                .build();
    }

    /**
     * 角色以 {@code ROLE_} 前缀写入，权限码原样写入，使 hasRole 与 hasAuthority 都能生效。
     */
    private List<String> resolveAuthorities(Long userId) {
        List<String> roleCodes = userService.listRoleCodes(userId);
        List<String> permissionCodes = userService.listPermissionCodes(userId);

        List<String> authorities = new ArrayList<>(roleCodes.size() + permissionCodes.size());
        for (String roleCode : roleCodes) {
            authorities.add("ROLE_" + roleCode);
        }
        authorities.addAll(permissionCodes);
        return authorities;
    }
}
