package com.kuma.boot.security.spring.access.expression;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service("authorizeCheck")
public class AuthorizeCheckService {
    private static final Logger log = LoggerFactory.getLogger(AuthorizeCheckService.class);
    private final RedisRepository redisRepository;

    public AuthorizeCheckService(@Nullable RedisRepository redisRepository) {
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
        log.info("[Authorize] user={}, authorities={}, required={}", name, authorities, Arrays.toString(annotation.value()));
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
        if (redisRepository == null) {
            return auth.getAuthorities() == null ? List.of() :
                    auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        }
        String cacheKey = "user:authorities:" + name;
        Object cached = redisRepository.get(cacheKey);
        if (cached instanceof UserEntity userEntity && userEntity.getAuthorities() != null) {
            return userEntity.getAuthorities();
        }
        // Jackson 未配置类型信息时反序列化为 LinkedHashMap，兼容处理
        if (cached instanceof Map<?, ?> map) {
            Object authList = map.get("authorities");
            if (authList instanceof List<?> list) {
                return list.stream().map(Object::toString).toList();
            }
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

        if (needAuthorities.length == 0) {
            throw new IllegalArgumentException("@Authorize annotation must specify at least one authority");
        }

        Set<String> userAuthorities = new HashSet<>(authorities);

        if (annotation.logical() == Authorize.Logical.OR) {
            // OR 模式：用户拥有任意一个所需权限即可
            return Arrays.stream(needAuthorities).anyMatch(r -> hasAuthority(userAuthorities, r));
        } else {
            // AND 模式：用户必须同时拥有所有所需权限
            return Arrays.stream(needAuthorities).allMatch(r -> hasAuthority(userAuthorities, r));
        }
    }

    /**
     * 判断用户是否拥有指定权限，支持通配符规则：
     * <ul>
     *   <li>精确匹配：用户有 {@code article:create} → 通过 {@code article:create}</li>
     *   <li>模块通配符：用户有 {@code article:*} → 通过所有 {@code article:xxx}</li>
     *   <li>超级权限：用户有 {@code *} → 通过所有校验</li>
     * </ul>
     */
    private boolean hasAuthority(Set<String> userAuthorities, String required) {
        // 精确匹配
        if (userAuthorities.contains(required)) {
            return true;
        }
        // 超级权限
        if (userAuthorities.contains(Permissions.ALL)) {
            return true;
        }
        // 模块通配符：required = "article:create"，检查用户是否有 "article:*"
        int colonIdx = required.indexOf(':');
        if (colonIdx > 0) {
            String moduleWildcard = required.substring(0, colonIdx + 1) + "*";
            return userAuthorities.contains(moduleWildcard);
        }
        return false;
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
