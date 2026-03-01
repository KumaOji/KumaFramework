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
import com.kuma.boot.common.utils.servlet.RequestUtils;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
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

        // 程序启动后修改认证信息上下文存储策略，支持子线程中获取认证信息 -Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL
        // MODE_THREALOCAL表示用户信息只能由当前线程访问。
        // MODE_INHERITABLETHREADLOCAL表示用户信息可以由当前线程及其子线程访问.。
        // MODE_GLOBAL表示用户信息没有线程限制，全局都可以访问，一般用于gui的开发中。
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        // 使用DelegatingSecurityContextRunnable创建线程
        // Runnable runnable = new DelegatingSecurityContextRunnable(() -> {
        //	// 线程处理逻辑
        //	// ...
        // });
        // new Thread(runnable).start();
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_DBA ROLE_DBA > ROLE_USER");
    }

    @Bean
    public StandardPermissionEvaluator standardPermissionEvaluator() {
        return new StandardPermissionEvaluator();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy, StandardPermissionEvaluator standardPermissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        expressionHandler.setPermissionEvaluator(standardPermissionEvaluator);
        return expressionHandler;
    }

    //	@Bean
    //	public MethodSecurityExpressionHandler methodSecurityExpressionHandler(ApplicationContext
    // context) {
    //		AuthorizeExpressionHandler expressionHandler = new AuthorizeExpressionHandler();
    //		expressionHandler.setApplicationContext(context);
    //		return expressionHandler;
    //	}

    @Bean(name = "pms")
    public PermissionVerifier permissionVerifier() {
        return new PermissionVerifier();
    }

    /**
     * 权限验证器
     *
     * @author kuma
     * @version 2022.06
     * @since 2022-06-08 11:28:55
     */
    public static class PermissionVerifier {

        // @PreAuthorize("@pms.hasPermission(#request, authentication, 'export')")
        public boolean hasPermission(
                HttpServletRequest req, Authentication authentication, String permission) {
            return false;
        }

        // @PreAuthorize("@pms.hasPermission('export')")
        public boolean hasPermission(String permission) {
            // 内部调用直接跳过
            String header = RequestUtils.getHeader(CommonConstants.KMC_FROM_INNER);
            if (Boolean.TRUE.equals(Boolean.valueOf(header))) {
                return true;
            }

            Collection<? extends GrantedAuthority> authorities =
                    SecurityUtils.getAuthentication().getAuthorities();
            if (CollUtil.isEmpty(authorities)) {
                return false;
            }

            for (GrantedAuthority grantedAuthority : authorities) {
                String authority = grantedAuthority.getAuthority();
                if (authority.contains(permission)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class StandardPermissionEvaluator implements PermissionEvaluator {

        /**
         * 用于SpEL表达式解析.
         */
        private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

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

        // 用于ACL的访问控制 @PreAuthorize("hasPermission(1, #batchDTO, 'batch')")
        @Override
        public boolean hasPermission(
                Authentication auth, Serializable targetId, String targetType, Object permission) {

            if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
                return false;
            }
            return hasPrivilege(
                    auth, targetType.toUpperCase(), permission.toString().toUpperCase());
        }

        private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
            // 内部微服务调用直接返回true
            String header = RequestUtils.getHeader(CommonConstants.KMC_FROM_INNER);
            if (Boolean.TRUE.equals(Boolean.valueOf(header))) {
                return true;
            }

            for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
            return false;
        }

        public boolean checkInner() {
            HttpServletRequest request = RequestUtils.getRequest();

            return true;
        }
    }
}
