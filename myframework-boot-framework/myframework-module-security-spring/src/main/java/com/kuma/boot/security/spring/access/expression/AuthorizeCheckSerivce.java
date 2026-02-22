/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.stereotype.Service
 */
package com.kuma.boot.security.spring.access.expression;

import com.kuma.boot.cache.redis.repository.RedisRepository;

import java.util.ArrayList;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Service;

@Service(value="authorizeCheck")
public class AuthorizeCheckSerivce {
    private final RedisRepository redisRepository;

    public AuthorizeCheckSerivce(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public boolean checkAuthority(RootObject root) {
        Authorize annotation;
        MethodInvocation methodInvocation = root.getMethodInvocation();
        if (methodInvocation == null) {
            return true;
        }
        if (methodInvocation.getMethod().isAnnotationPresent(Authorize.class)) {
            annotation = methodInvocation.getMethod().getAnnotation(Authorize.class);
        } else if (methodInvocation.getMethod().getDeclaringClass().isAnnotationPresent(Authorize.class)) {
            annotation = methodInvocation.getMethod().getDeclaringClass().getAnnotation(Authorize.class);
        } else {
            return true;
        }
        String name = root.getAuthentication().getName();
        UserEntity userEntity = (UserEntity)this.redisRepository.get(name);
        if (userEntity == null) {
            return false;
        }
        ArrayList authorities = new ArrayList();
        String[] needAuthorities = annotation.value();
        if (annotation.anyMatch()) {
            for (String needAuthority : needAuthorities) {
                if (!authorities.contains(needAuthority)) continue;
                return true;
            }
            return false;
        }
        for (String needAuthority : needAuthorities) {
            if (authorities.contains(needAuthority)) continue;
            return false;
        }
        return true;
    }

    public static class UserEntity {
    }
}

