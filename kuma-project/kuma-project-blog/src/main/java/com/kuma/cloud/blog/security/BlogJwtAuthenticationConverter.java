package com.kuma.cloud.blog.security;

import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.vo.LoginVO;
import com.kuma.cloud.blog.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 将 UAA 签发的 JWT 身份映射到 Blog 本地用户和权限模型。
 */
@Component
public class BlogJwtAuthenticationConverter
        implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private final UserService userService;
    private final String usernameClaim;

    public BlogJwtAuthenticationConverter(
            UserService userService,
            @Value("${blog.oauth2.username-claim:preferred_username}") String usernameClaim) {
        this.userService = userService;
        this.usernameClaim = usernameClaim;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString(usernameClaim);
        if (!StringUtils.hasText(username)) {
            username = jwt.getSubject();
        }

        User user = StringUtils.hasText(username) ? userService.getByUsername(username) : null;
        if (user == null) {
            throw invalidToken("UAA 用户未在 Blog 中建立映射", null);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw invalidToken("Blog 用户已被禁用", new DisabledException("Blog 用户已被禁用"));
        }

        LoginVO login = new LoginVO();
        login.setUserId(user.getId());
        login.setUsername(user.getUsername());
        login.setNickname(user.getNickname());
        login.setEmail(user.getEmail());
        login.setAdmin(user.getIsAdmin() != null && user.getIsAdmin() == 1);

        BlogUserDetails principal = new BlogUserDetails(login);
        return new UsernamePasswordAuthenticationToken(
                principal, jwt.getTokenValue(), principal.getAuthorities());
    }

    private InvalidBearerTokenException invalidToken(String message, AuthenticationException cause) {
        return cause == null
                ? new InvalidBearerTokenException(message)
                : new InvalidBearerTokenException(message, cause);
    }
}
