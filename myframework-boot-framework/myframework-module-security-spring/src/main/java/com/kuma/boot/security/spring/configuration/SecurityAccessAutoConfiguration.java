/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.dromara.hutool.core.collection.CollUtil
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.annotation.Bean
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.security.access.PermissionEvaluator
 *  org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
 *  org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
 *  org.springframework.security.access.hierarchicalroles.RoleHierarchy
 *  org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
 *  org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.core.context.SecurityContextHolder
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@AutoConfiguration
@EnableMethodSecurity(prePostEnabled=true, securedEnabled=true, jsr250Enabled=true, proxyTargetClass=true)
public class SecurityAccessAutoConfiguration
implements ApplicationContextAware,
InitializingBean {
    private ApplicationContext applicationContext;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(SecurityAccessAutoConfiguration.class, (String)"kuma-boot-starter-security-spring", (String[])new String[0]);
        SecurityContextHolder.setStrategyName((String)"MODE_INHERITABLETHREADLOCAL");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy((String)"ROLE_ADMIN > ROLE_DBA ROLE_DBA > ROLE_USER");
    }

    @Bean
    public StandardPermissionEvaluator standardPermissionEvaluator() {
        return new StandardPermissionEvaluator();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy, StandardPermissionEvaluator standardPermissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        expressionHandler.setPermissionEvaluator((PermissionEvaluator)standardPermissionEvaluator);
        return expressionHandler;
    }

    @Bean(name={"pms"})
    public PermissionVerifier permissionVerifier() {
        return new PermissionVerifier();
    }

    public static class StandardPermissionEvaluator
    implements PermissionEvaluator {
        private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

        public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
            if (auth == null || targetDomainObject == null || !(permission instanceof String)) {
                return false;
            }
            String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
            return this.hasPrivilege(auth, targetType, permission.toString().toUpperCase());
        }

        public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
            if (auth == null || targetType == null || !(permission instanceof String)) {
                return false;
            }
            return this.hasPrivilege(auth, targetType.toUpperCase(), permission.toString().toUpperCase());
        }

        private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
            String header = RequestUtils.getHeader((String)"ttc-from-inner");
            if (Boolean.TRUE.equals(Boolean.valueOf(header))) {
                return true;
            }
            for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
                if (!grantedAuth.getAuthority().contains(permission)) continue;
                return true;
            }
            return false;
        }

        public boolean checkInner() {
            HttpServletRequest request = RequestUtils.getRequest();
            return true;
        }
    }

    public static class PermissionVerifier {
        public boolean hasPermission(HttpServletRequest req, Authentication authentication, String permission) {
            return false;
        }

        public boolean hasPermission(String permission) {
            String header = RequestUtils.getHeader((String)"ttc-from-inner");
            if (Boolean.TRUE.equals(Boolean.valueOf(header))) {
                return true;
            }
            Collection authorities = SecurityUtils.getAuthentication().getAuthorities();
            if (CollUtil.isEmpty((Collection)authorities)) {
                return false;
            }
            for (GrantedAuthority grantedAuthority : authorities) {
                String authority = grantedAuthority.getAuthority();
                if (!authority.contains(permission)) continue;
                return true;
            }
            return false;
        }
    }
}

