package com.kuma.cloud.blog.security;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.security.spring.access.expression.RoleConstants;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.mapper.SysPermissionMapper;
import com.kuma.cloud.blog.service.UserService;
import com.kuma.cloud.blog.service.impl.PermissionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PermissionDefaultsTest {
    private final SysPermissionMapper mapper = mock(SysPermissionMapper.class);
    private final UserService users = mock(UserService.class);
    private final PermissionServiceImpl service = new PermissionServiceImpl(mapper,
            mock(RedisRepository.class), mock(JdbcTemplate.class), users);

    @Test
    void ordinaryAccountHasOnlyIdentityAndExplicitGrants() {
        when(mapper.selectRoleCodesByUserId(2L)).thenReturn(List.of(RoleConstants.USER));
        when(mapper.selectRolePermissionsByUserId(2L)).thenReturn(List.of());
        when(mapper.selectDirectPermissionsByUserId(2L)).thenReturn(List.of());
        var baseline = service.getUserAuthorities(2L);
        assertThat(baseline.getRoles()).containsExactly(RoleConstants.USER);
        assertThat(baseline.getRolePermissions()).isEmpty();
        assertThat(baseline.getDirectPermissions()).isEmpty();
        assertThat(service.loadAndCachePermissions(2L, "ordinary", 3600))
                .containsExactly(RoleConstants.USER);

        when(mapper.selectDirectPermissionsByUserId(2L))
                .thenReturn(List.of(BlogPermissions.CHAT_READ, BlogPermissions.CHAT_SEND));
        assertThat(service.loadAndCachePermissions(2L, "ordinary", 3600))
                .containsExactlyInAnyOrder(RoleConstants.USER, BlogPermissions.CHAT_READ, BlogPermissions.CHAT_SEND);
        when(mapper.selectDirectPermissionsByUserId(2L)).thenReturn(List.of());
        assertThat(service.loadAndCachePermissions(2L, "ordinary", 3600)).containsExactly(RoleConstants.USER);
    }

    @Test
    void adminFlagProvidesAllPermissionsEvenWhenRoleBindingsAreMissing() {
        var admin = new User();
        admin.setIsAdmin(1);
        when(users.getById(1L)).thenReturn(admin);
        var authorities = service.getUserAuthorities(1L);
        assertThat(authorities.getRoles()).contains(RoleConstants.ADMIN);
        assertThat(authorities.getRolePermissions()).containsExactlyInAnyOrderElementsOf(BlogPermissions.moduleWildcards());
        assertThat(service.loadAndCachePermissions(1L, "admin", 3600))
                .contains(RoleConstants.ADMIN).containsAll(BlogPermissions.moduleWildcards())
                .doesNotContain("*", BlogPermissions.ARTICLE_CREATE, BlogPermissions.CHAT_SEND);
    }

    @Test
    void adminRoleAlsoProvidesAllPermissions() {
        when(mapper.selectRoleCodesByUserId(3L)).thenReturn(List.of(RoleConstants.ADMIN));
        when(mapper.selectRolePermissionsByUserId(3L)).thenReturn(List.of("article:*", "article:create", "service:*"));
        assertThat(service.getUserAuthorities(3L).getRolePermissions())
                .containsAll(BlogPermissions.moduleWildcards()).contains("service:*")
                .allMatch(code -> code.endsWith(":*"));
        assertThat(service.loadAndCachePermissions(3L, "roleAdmin", 3600))
                .contains(RoleConstants.ADMIN, "service:*").containsAll(BlogPermissions.moduleWildcards())
                .doesNotContain("*", "article:create");
    }
}
