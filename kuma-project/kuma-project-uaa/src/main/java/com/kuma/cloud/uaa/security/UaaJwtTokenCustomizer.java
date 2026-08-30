package com.kuma.cloud.uaa.security;

import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.service.UaaUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 向 UAA 签发的 JWT 注入 Kuma 体系约定的声明。
 *
 * <p>Access Token 携带 {@code authorities}，业务方无需回查 UAA 即可完成鉴权；
 * ID Token 只携带身份画像，遵循 OIDC 对 profile 范围的约定。
 *
 * <p>声明值仅使用 JSON 基础类型（String / boolean / ArrayList），避免 Long 等类型被
 * {@code JdbcOAuth2AuthorizationService} 持久化后 Jackson 3 无法反序列化。
 *
 * @author kuma
 */
@Component
@RequiredArgsConstructor
public class UaaJwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UaaUserService userService;

    @Override
    public void customize(JwtEncodingContext context) {
        boolean accessToken = OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType());
        boolean idToken = OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue());
        if (!accessToken && !idToken) {
            return;
        }

        // client_credentials 模式下 principalName 是 clientId，不存在对应的自然人用户
        String username = context.getPrincipal().getName();
        UaaUser user = userService.getByUsername(username);
        if (user == null) {
            return;
        }

        context.getClaims().claim("preferred_username", user.getUsername());
        context.getClaims().claim("uid", String.valueOf(user.getId()));
        if (StringUtils.hasText(user.getTenantId())) {
            context.getClaims().claim("tenant_id", user.getTenantId());
        }

        if (StringUtils.hasText(user.getNickname())) {
            context.getClaims().claim("nickname", user.getNickname());
        }
        if (StringUtils.hasText(user.getAvatar())) {
            context.getClaims().claim("picture", user.getAvatar());
        }
        if (context.getAuthorizedScopes().contains(OidcScopes.EMAIL)
                && StringUtils.hasText(user.getEmail())) {
            context.getClaims().claim("email", user.getEmail());
        }

        if (accessToken) {
            List<String> authorities = new ArrayList<>(context.getPrincipal().getAuthorities().size());
            for (GrantedAuthority authority : context.getPrincipal().getAuthorities()) {
                authorities.add(authority.getAuthority());
            }
            context.getClaims().claim("authorities", authorities);
            context.getClaims().claim("mfa", user.isMfaEnabled());
        }
    }
}
