/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.utils.servlet.RequestUtils;
import com.kuma.boot.security.spring.access.expression.AuthorizeExpressionHandler;
import com.kuma.boot.security.spring.access.expression.RoleConstants;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import java.io.Serializable;
import java.util.Collection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * MethodSecurityConfig
 *
 * @author kuma
 * @version 2022.03
 * @since 2021/10/13 15:40
 */
@AutoConfiguration
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true,
        proxyTargetClass = true)
public class SecurityAccessAutoConfiguration implements ApplicationContextAware, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(SecurityAccessAutoConfiguration.class, StarterNameConstants.SECURITY_SPRINGSECURITY_STARTER);

        // 修改 SecurityContext 传播策略：允许子线程继承父线程的认证信息。
        // 等效于 JVM 参数 -Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                RoleConstants.ADMIN + " > " + RoleConstants.DBA + "\n" +
                RoleConstants.DBA + " > " + RoleConstants.USER);
    }

    @Bean
    public StandardPermissionEvaluator standardPermissionEvaluator() {
        return new StandardPermissionEvaluator();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy, StandardPermissionEvaluator standardPermissionEvaluator) {
        AuthorizeExpressionHandler expressionHandler = new AuthorizeExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        expressionHandler.setRoleHierarchy(roleHierarchy);
        expressionHandler.setPermissionEvaluator(standardPermissionEvaluator);
        return expressionHandler;
    }

    @Bean(name = "pms")
    public PermissionVerifier permissionVerifier() {
        return new PermissionVerifier();
    }

    /**
     * 权限验证器，供 SpEL 表达式使用：{@code @PreAuthorize("@pms.hasPermission('export')")}
     */
    public static class PermissionVerifier {

        // @PreAuthorize("@pms.hasPermission('export')")
        public boolean hasPermission(String permission) {
            if (isInnerRequest()) {
                return true;
            }

            Authentication authentication = SecurityUtils.getAuthentication();
            if (authentication == null) {
                return false;
            }

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (CollUtil.isEmpty(authorities)) {
                return false;
            }

            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().contains(permission)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class StandardPermissionEvaluator implements PermissionEvaluator {

        // 普通的targetDomainObject判断 @PreAuthorize("hasPermission(#batchDTO, 'batch')")
        @Override
        public boolean hasPermission(
                Authentication auth, Object targetDomainObject, Object permission) {
            if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
                return false;
            }
            String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
            return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
        }

        // 用于ACL的访问控制 @PreAuthorize("hasPermission(1, 'TargetType', 'batch')")
        @Override
        public boolean hasPermission(
                Authentication auth, Serializable targetId, String targetType, Object permission) {
            if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
                return false;
            }
            return hasPrivilege(auth, targetType.toUpperCase(), permission.toString().toUpperCase());
        }

        private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
            if (isInnerRequest()) {
                return true;
            }
            for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean isInnerRequest() {
        String header = RequestUtils.getHeader(CommonConstants.KMC_FROM_INNER);
        return Boolean.parseBoolean(header);
    }
}
