package com.kuma.cloud.uaa.security;

import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.service.UaaUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * {@code /userinfo} 响应映射。
 *
 * <p>默认实现直接回放 ID Token 的声明，这里改为按 Access Token 的 principalName 实时回查数据库，
 * 使用户资料修改后无需重新登录即可生效，同时严格按已授权 scope 裁剪返回字段。
 *
 * @author kuma
 */
@Component
@RequiredArgsConstructor
public class UaaOidcUserInfoMapper
        implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

    private final UaaUserService userService;

    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext context) {
        String username = context.getAuthorization().getPrincipalName();
        Set<String> scopes = context.getAccessToken().getScopes();

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put(StandardClaimNames.SUB, username);

        UaaUser user = userService.getByUsername(username);
        if (user == null) {
            return new OidcUserInfo(claims);
        }

        if (scopes.contains(OidcScopes.PROFILE)) {
            claims.put(StandardClaimNames.PREFERRED_USERNAME, user.getUsername());
            putIfPresent(claims, StandardClaimNames.NICKNAME, user.getNickname());
            putIfPresent(claims, StandardClaimNames.NAME, user.getNickname());
            putIfPresent(claims, StandardClaimNames.PICTURE, user.getAvatar());
            if (user.getUpdateTime() != null) {
                claims.put(StandardClaimNames.UPDATED_AT, user.getUpdateTime().toString());
            }
        }
        if (scopes.contains(OidcScopes.EMAIL)) {
            putIfPresent(claims, StandardClaimNames.EMAIL, user.getEmail());
            claims.put(StandardClaimNames.EMAIL_VERIFIED, StringUtils.hasText(user.getEmail()));
        }
        if (scopes.contains(OidcScopes.PHONE)) {
            putIfPresent(claims, StandardClaimNames.PHONE_NUMBER, user.getPhone());
        }
        return new OidcUserInfo(claims);
    }

    private void putIfPresent(Map<String, Object> claims, String name, String value) {
        if (StringUtils.hasText(value)) {
            claims.put(name, value);
        }
    }
}
