package com.kuma.cloud.blog.support;

import com.kuma.boot.security.spring.access.expression.RoleConstants;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时把 {@link BlogPermissions#catalog()} 同步进 {@code sys_permission}，
 * 并让 ROLE_ADMIN / is_admin 用户默认拥有全部权限（含知识库）。
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class BlogPermissionInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PermissionService permissionService;

    @Override
    public void run(ApplicationArguments args) {
        int inserted = syncCatalog();
        Long adminRoleId = ensureAdminRole();
        int bound = grantAllToAdmin(adminRoleId);
        int linked = linkAdminUsers(adminRoleId);
        evictAdminCaches();
        log.info("权限清单已同步：新增 {} 项，ROLE_ADMIN 绑定 {} 条，管理员用户关联 {} 人",
                inserted, bound, linked);
    }

    private int syncCatalog() {
        boolean hasTimestamps = hasColumn("sys_permission", "create_time");
        int inserted = 0;
        for (BlogPermissions.PermissionDef def : BlogPermissions.catalog()) {
            if (findPermissionId(def.code()) != null) {
                continue;
            }
            if (hasTimestamps) {
                jdbcTemplate.update(
                        """
                        INSERT INTO sys_permission (code, name, module, description, create_time, update_time)
                        VALUES (?, ?, ?, ?, NOW(), NOW())
                        """,
                        def.code(), def.name(), def.module(), def.description());
            } else {
                jdbcTemplate.update(
                        "INSERT INTO sys_permission (code, name, module, description) VALUES (?, ?, ?, ?)",
                        def.code(), def.name(), def.module(), def.description());
            }
            inserted++;
        }
        return inserted;
    }

    private Long findPermissionId(String code) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT id FROM sys_permission WHERE code = ?",
                (rs, rowNum) -> rs.getLong(1),
                code);
        return ids.isEmpty() ? null : ids.getFirst();
    }

    private Long ensureAdminRole() {
        List<Long> ids = jdbcTemplate.query(
                "SELECT id FROM sys_role WHERE code = ?",
                (rs, rowNum) -> rs.getLong(1),
                RoleConstants.ADMIN);
        if (!ids.isEmpty()) {
            return ids.getFirst();
        }
        if (hasColumn("sys_role", "create_time")) {
            jdbcTemplate.update(
                    "INSERT INTO sys_role (code, name, create_time, update_time) VALUES (?, '管理员', NOW(), NOW())",
                    RoleConstants.ADMIN);
        } else {
            jdbcTemplate.update(
                    "INSERT INTO sys_role (code, name) VALUES (?, '管理员')",
                    RoleConstants.ADMIN);
        }
        return jdbcTemplate.queryForObject(
                "SELECT id FROM sys_role WHERE code = ?", Long.class, RoleConstants.ADMIN);
    }

    private int grantAllToAdmin(Long adminRoleId) {
        return jdbcTemplate.update(
                """
                INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
                SELECT ?, p.id FROM sys_permission p
                WHERE NOT EXISTS (
                    SELECT 1 FROM sys_role_permission rp
                    WHERE rp.role_id = ? AND rp.permission_id = p.id)
                """,
                adminRoleId, adminRoleId);
    }

    private int linkAdminUsers(Long adminRoleId) {
        return jdbcTemplate.update(
                """
                INSERT IGNORE INTO sys_user_role (user_id, role_id)
                SELECT u.id, ?
                FROM user u
                WHERE u.is_admin = 1
                  AND NOT EXISTS (
                      SELECT 1 FROM sys_user_role ur
                      WHERE ur.user_id = u.id AND ur.role_id = ?)
                """,
                adminRoleId, adminRoleId);
    }

    private void evictAdminCaches() {
        jdbcTemplate.query(
                "SELECT username FROM user WHERE is_admin = 1",
                (rs, rowNum) -> rs.getString(1))
                .forEach(permissionService::evictCache);
    }

    private boolean hasColumn(String table, String column) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?
                """,
                Integer.class, table, column);
        return count != null && count > 0;
    }
}
