package com.kuma.cloud.blog.websocket;

import com.kuma.cloud.blog.domain.vo.LoginVO;
import com.kuma.cloud.blog.domain.vo.UserAuthoritiesVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.security.BlogUserDetails;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import com.kuma.cloud.blog.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;
import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatAccessService {
    private final PermissionService permissionService;
    private final ChatBlacklistService blacklistService;

    public boolean canAccess(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof BlogUserDetails user)) {
            return false;
        }
        require(user.getLoginResponse(), permission);
        return true;
    }

    public void require(LoginVO login, String permission) {
        if (login == null || login.getUserId() == null) {
            throw new AccessDeniedException("请先登录后再使用聊天");
        }
        if (blacklistService.isBlocked(login.getEmail())) {
            throw new AccessDeniedException("当前用户已被禁止聊天");
        }
        UserAuthoritiesVO authorities = permissionService.getUserAuthorities(login.getUserId());
        boolean allowed = Stream.of(authorities.getRoles(), authorities.getRolePermissions(), authorities.getDirectPermissions())
                .filter(Objects::nonNull).flatMap(Collection::stream)
                .anyMatch(code -> code.equals(permission) || code.equals(BlogPermissions.CHAT_ALL)
                        || code.equals("*") || code.equals("ROLE_ADMIN"));
        if (!allowed) throw new AccessDeniedException("缺少聊天权限：" + permission);
    }
}
