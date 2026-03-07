package com.kuma.cloud.blog.service;

import java.util.List;

/**
 * 权限加载服务：将用户的角色权限 + 直接授权权限合并后写入 Redis 缓存。
 *
 * <p>三类用户的权限来源：
 * <ul>
 *   <li><b>管理员</b>：通过 sys_user_role 关联 ROLE_ADMIN，角色权限包含 article:* 等通配符</li>
 *   <li><b>普通用户</b>：通过 sys_user_role 关联 ROLE_USER，角色权限为只读</li>
 *   <li><b>授权用户</b>：在 ROLE_USER 基础上，sys_user_permission 额外授予具体权限码</li>
 * </ul>
 */
public interface PermissionService {

    /**
     * 加载用户全量权限列表（角色权限 ∪ 直接授权权限），写入 Redis。
     *
     * @param userId 用户ID
     * @return 合并后的权限码列表（含角色码 ROLE_xxx 和权限码 module:action）
     */
    List<String> loadAndCachePermissions(Long userId, String username, long expireSeconds);

    /**
     * 为用户直接授予某个权限（授权用户管理）。
     * 管理员调用此接口给特定用户赋权，无需改变其角色。
     *
     * @param userId       被授权用户ID
     * @param permissionId 权限ID
     * @param grantedBy    操作人用户ID
     */
    void grantPermission(Long userId, Long permissionId, Long grantedBy);

    /**
     * 撤销用户的某个直接授权权限。
     */
    void revokePermission(Long userId, Long permissionId);

    /**
     * 将用户的 Redis 权限缓存失效（权限变更后调用）。
     */
    void evictCache(String username);
}
