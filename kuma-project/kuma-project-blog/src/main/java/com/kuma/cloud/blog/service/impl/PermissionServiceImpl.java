package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.security.spring.access.expression.AuthorizeCheckService;
import com.kuma.cloud.blog.domain.entity.SysPermission;
import com.kuma.cloud.blog.mapper.SysPermissionMapper;
import com.kuma.cloud.blog.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionMapper permissionMapper;
    private final RedisRepository redisRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String CACHE_KEY_PREFIX = "user:authorities:";

    @Override
    public List<SysPermission> listAll() {
        return permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getModule, SysPermission::getCode));
    }

    @Override
    public List<SysPermission> listUserDirectPermissions(Long userId) {
        return permissionMapper.selectDirectPermissionEntitiesByUserId(userId);
    }

    @Override
    public List<String> loadAndCachePermissions(Long userId, String username, long expireSeconds) {
        // 1. 角色码（ROLE_ADMIN / ROLE_USER）
        List<String> roleCodes = permissionMapper.selectRoleCodesByUserId(userId);

        // 2. 角色权限码（通过角色关联的权限）
        List<String> rolePermissions = permissionMapper.selectRolePermissionsByUserId(userId);

        // 3. 直接授权权限码（授权用户的个人权限，去重合并）
        List<String> directPermissions = permissionMapper.selectDirectPermissionsByUserId(userId);

        // 合并：角色码 + 角色权限 + 直接授权（用 Set 去重保证唯一性）
        Set<String> all = new LinkedHashSet<>();
        all.addAll(roleCodes);
        all.addAll(rolePermissions);
        all.addAll(directPermissions);

        List<String> authorities = new ArrayList<>(all);

        // 写入 Redis 缓存
        AuthorizeCheckService.UserEntity entity = new AuthorizeCheckService.UserEntity();
        entity.setAuthorities(authorities);
        String cacheKey = CACHE_KEY_PREFIX + username;
        redisRepository.set(cacheKey, entity);
        redisRepository.expire(cacheKey, expireSeconds);

        log.debug("User [{}] permissions loaded: {}", username, authorities);
        return authorities;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantPermission(Long userId, Long permissionId, Long grantedBy) {
        jdbcTemplate.update(
                "INSERT IGNORE INTO sys_user_permission (user_id, permission_id, granted_by) VALUES (?, ?, ?)",
                userId, permissionId, grantedBy);
        log.info("Permission [{}] granted to user [{}] by [{}]", permissionId, userId, grantedBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokePermission(Long userId, Long permissionId) {
        jdbcTemplate.update(
                "DELETE FROM sys_user_permission WHERE user_id = ? AND permission_id = ?",
                userId, permissionId);
        log.info("Permission [{}] revoked from user [{}]", permissionId, userId);
    }

    @Override
    public void evictCache(String username) {
        redisRepository.del(CACHE_KEY_PREFIX + username);
        log.warn("Permission cache evicted for user [{}]", username);
    }
}
