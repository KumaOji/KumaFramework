package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.security.spring.access.expression.AuthorizeCheckService;
import com.kuma.cloud.blog.domain.entity.SysPermission;
import com.kuma.cloud.blog.domain.vo.UserAuthoritiesVO;
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
import java.util.concurrent.CompletableFuture;

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
    public UserAuthoritiesVO getUserAuthorities(Long userId) {
        UserAuthoritiesVO vo = new UserAuthoritiesVO();
        vo.setRoles(permissionMapper.selectRoleCodesByUserId(userId));
        vo.setRolePermissions(permissionMapper.selectRolePermissionsByUserId(userId));
        vo.setDirectPermissions(permissionMapper.selectDirectPermissionsByUserId(userId));
        return vo;
    }

    @Override
    public List<String> loadAndCachePermissions(Long userId, String username, long expireSeconds) {
        // 三条 DB 查询并行执行（虚拟线程 + CompletableFuture，IO 等待重叠）
        CompletableFuture<List<String>> roleCodesFut =
                CompletableFuture.supplyAsync(() -> permissionMapper.selectRoleCodesByUserId(userId));
        CompletableFuture<List<String>> rolePermsFut =
                CompletableFuture.supplyAsync(() -> permissionMapper.selectRolePermissionsByUserId(userId));
        CompletableFuture<List<String>> directPermsFut =
                CompletableFuture.supplyAsync(() -> permissionMapper.selectDirectPermissionsByUserId(userId));

        CompletableFuture.allOf(roleCodesFut, rolePermsFut, directPermsFut).join();

        Set<String> all = new LinkedHashSet<>();
        all.addAll(roleCodesFut.join());
        all.addAll(rolePermsFut.join());
        all.addAll(directPermsFut.join());

        List<String> authorities = new ArrayList<>(all);

        // 写入 Redis：set + expire 合并为带 TTL 的单次 set
        AuthorizeCheckService.UserEntity entity = new AuthorizeCheckService.UserEntity();
        entity.setAuthorities(authorities);
        String cacheKey = CACHE_KEY_PREFIX + username;
        redisRepository.set(cacheKey, entity, expireSeconds);

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
