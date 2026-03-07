package com.kuma.boot.security.spring.access.expression;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service("authorizeCheck")
public class AuthorizeCheckService {
    private final RedisRepository redisRepository;

    public AuthorizeCheckService(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public boolean checkAuthority(RootObject root) {
        MethodInvocation methodInvocation = root.getMethodInvocation();
        if (methodInvocation == null) {
            return true;
        }

        Authorize annotation = getAuthorizeAnnotation(methodInvocation);
        if (annotation == null) {
            return true;
        }

        Authentication auth = root.getAuthentication();
        if (auth == null) {
            return false;
        }

        String name = auth.getName();
        List<String> authorities = getAuthorities(name, auth);
        return checkAuthorization(authorities, annotation);
    }

    private Authorize getAuthorizeAnnotation(MethodInvocation methodInvocation) {
        Authorize annotation = methodInvocation.getMethod().getAnnotation(Authorize.class);
        if (annotation != null) {
            return annotation;
        }
        return methodInvocation.getMethod().getDeclaringClass().getAnnotation(Authorize.class);
    }

    private List<String> getAuthorities(String name, Authentication auth) {
        // Try to get from Redis cache first (namespace-aware key)
        String cacheKey = "user:authorities:" + name;
        Object cached = redisRepository.get(cacheKey);
        if (cached instanceof UserEntity userEntity && userEntity.getAuthorities() != null) {
            return userEntity.getAuthorities();
        }

        // Fall back to extracting from Authentication (supports Token-based auth)
        if (auth.getAuthorities() == null) {
            return List.of();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    private boolean checkAuthorization(List<String> authorities, Authorize annotation) {
        String[] needAuthorities = annotation.value();

        // Validate annotation configuration
        if (needAuthorities.length == 0) {
            throw new IllegalArgumentException("@Authorize annotation must specify at least one authority");
        }

        Set<String> userAuthorities = new HashSet<>(authorities);
        Set<String> requiredAuthorities = new HashSet<>(Arrays.asList(needAuthorities));

        if (annotation.logical() == Authorize.Logical.OR) {
            // OR 模式：用户拥有任意一个所需权限即可
            return userAuthorities.stream().anyMatch(requiredAuthorities::contains);
        } else {
            // AND 模式：用户必须同时拥有所有所需权限
            return requiredAuthorities.stream().allMatch(userAuthorities::contains);
        }
    }

    public static class UserEntity {
        private List<String> authorities;

        public List<String> getAuthorities() {
            return authorities;
        }

        public void setAuthorities(List<String> authorities) {
            this.authorities = authorities;
        }
    }
}
