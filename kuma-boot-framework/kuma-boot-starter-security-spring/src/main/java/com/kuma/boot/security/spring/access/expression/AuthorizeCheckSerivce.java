package com.kuma.boot.security.spring.access.expression;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Service;

@Service("authorizeCheck")
public class AuthorizeCheckSerivce {
    private final RedisRepository redisRepository;

    public AuthorizeCheckSerivce(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public boolean checkAuthority(RootObject root) {
        MethodInvocation methodInvocation = root.getMethodInvocation();
        if (methodInvocation == null) {
            return true;
        } else {
            Authorize annotation;
            if (methodInvocation.getMethod().isAnnotationPresent(Authorize.class)) {
                annotation = methodInvocation.getMethod().getAnnotation(Authorize.class);
            } else {
                if (!methodInvocation.getMethod().getDeclaringClass().isAnnotationPresent(Authorize.class)) {
                    return true;
                }
                annotation = methodInvocation.getMethod().getDeclaringClass().getAnnotation(Authorize.class);
            }

            String name = root.getAuthentication().getName();
            UserEntity userEntity = (UserEntity) this.redisRepository.get(name);
            if (userEntity == null) {
                return false;
            } else {
                List<String> authorities = userEntity.getAuthorities();
                if (authorities == null) {
                    authorities = new ArrayList<>();
                }
                String[] needAuthorities = annotation.value();
                if (annotation.anyMatch()) {
                    for (String needAuthority : needAuthorities) {
                        if (authorities.contains(needAuthority)) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    for (String needAuthority : needAuthorities) {
                        if (!authorities.contains(needAuthority)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
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
