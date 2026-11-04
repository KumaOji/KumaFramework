package com.kuma.cloud.blog.security;

import com.kuma.cloud.blog.service.PermissionService;
import com.kuma.cloud.blog.support.BlogPermissionInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BlogPermissionInitializerTest {
    @Test
    void startupNormalizesDefaultRolesPreservesDirectGrantsAndInvalidatesAllUserCaches() {
        var jdbc = mock(JdbcTemplate.class);
        var permissions = mock(PermissionService.class);
        when(jdbc.queryForObject(contains("information_schema.COLUMNS"), eq(Integer.class), anyString(), anyString()))
                .thenReturn(1);
        when(jdbc.query(anyString(), org.mockito.ArgumentMatchers.<RowMapper<Long>>any(), anyString()))
                .thenAnswer(invocation -> List.of("ROLE_ADMIN".equals(invocation.getArgument(2)) ? 1L : 2L));
        when(jdbc.query(eq("SELECT username FROM user"), org.mockito.ArgumentMatchers.<RowMapper<String>>any()))
                .thenReturn(List.of("admin", "ordinary"));

        new BlogPermissionInitializer(jdbc, permissions).run(null);

        verify(jdbc).update("DELETE FROM sys_role_permission WHERE role_id = ?", 2L);
        verify(jdbc).update(contains("SET u.is_admin = 1"), eq(1L));
        verify(jdbc).update(contains("COALESCE(u.is_admin, 0) <> 1"), eq(2L));
        verify(jdbc).update(contains("SELECT ?, p.id FROM sys_permission p"), eq(1L), eq(1L));
        verify(jdbc).update(contains("p.code NOT LIKE '%:*'"), eq(1L));
        verify(jdbc, never()).update(contains("DELETE FROM sys_user_permission"));
        verify(permissions).evictCache("admin");
        verify(permissions).evictCache("ordinary");
        for (var definition : BlogPermissions.catalog()) {
            verify(jdbc).update(eq("UPDATE sys_permission SET name = ?, module = ?, description = ? WHERE code = ?"),
                    eq(definition.name()), eq(definition.module()), eq(definition.description()), eq(definition.code()));
        }
    }
}
