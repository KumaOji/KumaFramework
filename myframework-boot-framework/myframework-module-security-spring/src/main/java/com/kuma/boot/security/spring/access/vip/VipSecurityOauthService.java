/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 */
package com.kuma.boot.security.spring.access.vip;

import com.kuma.boot.cache.redis.repository.RedisRepository;

import java.util.HashSet;
import java.util.Set;

public class VipSecurityOauthService {
    private final RedisRepository redisRepository;

    public VipSecurityOauthService(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public Set<PermRoleEntity> loadPerms() {
        HashSet<PermRoleEntity> permRoleEntitySet = new HashSet<PermRoleEntity>();
        return permRoleEntitySet;
    }
}

