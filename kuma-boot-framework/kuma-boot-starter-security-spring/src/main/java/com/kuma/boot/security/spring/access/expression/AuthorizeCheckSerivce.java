//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
            com.kuma.boot.security.spring.access.expression.Authorize annotation;
            if (methodInvocation.getMethod().isAnnotationPresent(com.kuma.boot.security.spring.access.expression.Authorize.class)) {
                annotation = (com.kuma.boot.security.spring.access.expression.Authorize)methodInvocation.getMethod().getAnnotation(com.kuma.boot.security.spring.access.expression.Authorize.class);
            } else {
                if (!methodInvocation.getMethod().getDeclaringClass().isAnnotationPresent(com.kuma.boot.security.spring.access.expression.Authorize.class)) {
                    return true;
                }

                annotation = (com.kuma.boot.security.spring.access.expression.Authorize)methodInvocation.getMethod().getDeclaringClass().getAnnotation(com.kuma.boot.security.spring.access.expression.Authorize.class);
            }

            String name = root.getAuthentication().getName();
            UserEntity userEntity = (UserEntity)this.redisRepository.get(name);
            if (userEntity == null) {
                return false;
            } else {
                List<String> authorities = new ArrayList();
                String[] needAuthorities = annotation.value();
                if (annotation.anyMatch()) {
                    for(String needAuthority : needAuthorities) {
                        if (authorities.contains(needAuthority)) {
                            return true;
                        }
                    }

                    return false;
                } else {
                    for(String needAuthority : needAuthorities) {
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
    }
}
