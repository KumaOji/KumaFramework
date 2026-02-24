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

package com.kuma.boot.security.spring.authorization;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.access.security.SecurityConfigAttribute;
import com.kuma.boot.security.spring.access.security.SecurityRequest;
import com.kuma.boot.security.spring.access.security.SecurityRequestMatcher;
import com.kuma.boot.security.spring.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * <p>
 * Spring Security 6 授权管理
 * <p>
 * 1. 由原来的 AccessDecisionManager 和 AccessDecisionVoter，变更为使用 {@link AuthorizationManager}
 * <p>
 * 2. 原来的 SecurityMetadataSource 已经不再使用。其实想要自己扩展，基本逻辑还是一致。只不过给使用者更大的扩展度和灵活度。
 * <p>
 * 3. 原来的
 * <code>FilterSecurityInterceptor</code>，已经不再使用。改为使用
 * {@link org.springframework.security.web.access.intercept.AuthorizationFilter}
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:00:31
 */
public class SecurityAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(SecurityAuthorizationManager.class);

    /**
     * 安全存储元数据来源
     */
    private final SecurityMetadataSourceStorage securityMetadataSourceStorage;

    /**
     * 安全匹配器配置
     */
    private final SecurityMatcherConfigurer securityMatcherConfigurer;

    /**
     * 安全授权管理器
     *
     * @param securityMetadataSourceStorage 安全存储元数据来源
     * @param securityMatcherConfigurer     安全匹配器配置
     * @since 2023-07-04 10:00:31
     */
    public SecurityAuthorizationManager(
            SecurityMetadataSourceStorage securityMetadataSourceStorage,
            SecurityMatcherConfigurer securityMatcherConfigurer) {
        this.securityMetadataSourceStorage = securityMetadataSourceStorage;
        this.securityMatcherConfigurer = securityMatcherConfigurer;
    }

    /**
     * 检查
     *
     * @param authenticationSupplier 身份验证
     * @param object                 对象
     * @return {@link AuthorizationDecision }
     * @since 2023-07-04 10:00:31
     */
//    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext object) {

        final HttpServletRequest request = object.getRequest();

        String url = request.getRequestURI();
        String method = request.getMethod();

        if (WebUtils.isStaticResources(url)) {
            log.info("Is static resource : [{}], Passed!", url);
            return new AuthorizationDecision(true);
        }

        if (WebUtils.isPathMatch(securityMatcherConfigurer.getPermitAllList(), url)) {
            log.info("Is white list resource : [{}], Passed!", url);
            return new AuthorizationDecision(true);
        }

        String innerFlag = request.getHeader(CommonConstants.KMC_FROM_INNER);
        if (StringUtils.isNotBlank(innerFlag)) {
            log.info("Is inner invoke : [{}], Passed!", url);
            return new AuthorizationDecision(true);
        }

        if (WebUtils.isPathMatch(securityMatcherConfigurer.getHasAuthenticatedList(), url)) {
            log.info("Is has authenticated resource : [{}]", url);
            return new AuthorizationDecision(authenticationSupplier.get().isAuthenticated());
        }

        List<SecurityConfigAttribute> configAttributes = findConfigAttribute(url, method, request);
        if (CollectionUtils.isEmpty(configAttributes)) {
            log.info("NO PRIVILEGES : [{}].", url);

            if (!securityMatcherConfigurer.getAuthorizationProperties().getStrict()) {
                Authentication authentication;
                try {
                    authentication = authenticationSupplier.get();
                } catch (Exception e) {
                    // todo 此处需要修改 目前就是在oauth2 device请求时会抛出异常
                    // log.error("获取认证消息失败", e);
                    return new AuthorizationDecision(true);
                }

                if (authentication instanceof AnonymousAuthenticationToken) {
                    log.info("anonymousAuthenticationToken : {}", url);
                    return new AuthorizationDecision(false);
                }

                if (authentication.isAuthenticated()) {
                    log.info("Request is authenticated: [{}].", url);
                    return new AuthorizationDecision(true);
                }
            }

            return new AuthorizationDecision(false);
        }

        for (SecurityConfigAttribute configAttribute : configAttributes) {
            WebExpressionAuthorizationManager webExpressionAuthorizationManager =
                    new WebExpressionAuthorizationManager(configAttribute.getAttribute());
//            AuthorizationDecision decision =
//                    webExpressionAuthorizationManager.check(authenticationSupplier, object);
//            if (decision.isGranted()) {
//                log.info("Request [{}] is authorized!", object.getRequest().getRequestURI());
//                return decision;
//            }
        }

        return new AuthorizationDecision(false);
    }

    /**
     * 找到配置属性
     *
     * @param url     url
     * @param method  方法
     * @param request 请求
     * @return {@link List }<{@link SecurityConfigAttribute }>
     * @since 2023-07-04 10:00:31
     */
    private List<SecurityConfigAttribute> findConfigAttribute(
            String url, String method, HttpServletRequest request) {

        log.info("Current Request is : [{}] - [{}]", url, method);

        List<SecurityConfigAttribute> configAttributes =
                this.securityMetadataSourceStorage.getConfigAttribute(url, method);
        if (CollectionUtils.isNotEmpty(configAttributes)) {
            log.info("Get configAttributes from local storage for : [{}] - [{}]", url, method);
            return configAttributes;
        } else {
            LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatible =
                    this.securityMetadataSourceStorage.getCompatible();
            if (MapUtils.isNotEmpty(compatible)) {
                // 支持含有**通配符的路径搜索
                for (Map.Entry<SecurityRequest, List<SecurityConfigAttribute>> entry :
                        compatible.entrySet()) {
                    SecurityRequestMatcher requestMatcher =
                            new SecurityRequestMatcher(entry.getKey());
                    if (requestMatcher.matches(request)) {
                        log.info(
                                "Request match the wildcard [{}] - [{}]",
                                entry.getKey(),
                                entry.getValue());
                        return entry.getValue();
                    }
                }
            }
        }

        return null;
    }

    /**
     * url烫发负载
     */
    //	@Autowired
    //	private UrlSecurityPermsLoad urlPermsLoad;

    /**
     * 经理
     */
    private ScriptEngineManager manager = new ScriptEngineManager();

    /**
     * 引擎
     */
    private ScriptEngine engine = manager.getEngineByName("js");

    /**
     * 模式
     */
    public static final Pattern pattern = Pattern.compile("(\\w+)");

    /**
     * 访问决策选民
     *
     * @param authentication 身份验证
     * @param object         对象
     * @param attributes     属性
     * @return {@link AuthorizationDecision }
     * @since 2023-07-04 10:00:32
     */
//    public AuthorizationDecision accessDecisionVoter(
//            Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
//        if (authentication == null) {
//            return new AuthorizationDecision(false);
//        }
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//        for (ConfigAttribute attribute : attributes) {
//            if (attribute.getAttribute() == null) {
//                continue;
//            }
//            // Attempt to find a matching granted authority
//            for (GrantedAuthority authority : authorities) {
//                if (attribute.getAttribute().equals(authority.getAuthority())) {
//                    return new AuthorizationDecision(true);
//                }
//            }
//        }
//
//        // 自定义的权限控制
//        FilterInvocation fi = (FilterInvocation) object;
//        String url = fi.getRequestUrl();
//        //		String matchRoles = urlPermsLoad.findMatchRoles(url);
//        String matchRoles = "";
//        if (StringUtils.isBlank(matchRoles)) {
//            return new AuthorizationDecision(false);
//        }
//        if (matchRoles.contains("anon")) {
//            return new AuthorizationDecision(true);
//        }
//
//        // 如果路径没有角色配置,则默认是可以被访问的, 只要登录了就行
//        if (matchRoles.contains("authc") && "anonymousUser".equals(authentication.getPrincipal())) {
//            return new AuthorizationDecision(false);
//        }
//
//        final String originScript = matchRoles;
//
//        // 权限解析, 用户有的替换为 true, 没有的替换成 false
//        for (GrantedAuthority authority : authorities) {
//            final String userHasRole = authority.getAuthority();
//            matchRoles = matchRoles.replaceAll(userHasRole, "true");
//        }
//
//        // 用户没有的权限替换为 flase
//        // final List<String> allRoles =
//        // roleService.findRoles().stream().toList();
//        final List<String> allRoles = new ArrayList<>();
//        for (String role : allRoles) {
//            matchRoles = matchRoles.replaceAll(role, "false");
//        }
//
//        // 找不到的替换为 undefined
//        try {
//            final Matcher matcher = pattern.matcher(originScript);
//            while (matcher.find()) {
//                for (int i = 0; i < matcher.groupCount(); i++) {
//                    matchRoles = matchRoles.replaceAll(matcher.group(i), "undefined");
//                }
//            }
//        } catch (Exception e) {
//            LogUtils.warn("正则匹配错误,可能脚本存在问题:{}", e.getMessage());
//        }
//
//        try {
//            final Object eval = engine.eval(matchRoles);
//            if (!(eval instanceof Boolean)) {
//                LogUtils.error("脚本执行失败,返回值非 Bool 类型,默认是禁止访问:{},{}", originScript, matchRoles);
//                return new AuthorizationDecision(false);
//            }
//            if (((Boolean) eval)) {}
//
//            return ((Boolean) eval)
//                    ? new AuthorizationDecision(true)
//                    : new AuthorizationDecision(false);
//        } catch (ScriptException e) {
//            LogUtils.error(
//                    "脚本执行错误:[origin:{}][parse:{}][message:{}]",
//                    originScript,
//                    matchRoles,
//                    e.getMessage());
//            return new AuthorizationDecision(false);
//        }
//    }

    /**
     * 访问决策管理器
     *
     * @param authentication   身份验证
     * @param object           对象
     * @param configAttributes 配置属性
     * @since 2023-07-04 10:00:32
     */
//    public void accessDecisionManager(
//            Authentication authentication,
//            Object object,
//            Collection<ConfigAttribute> configAttributes) {
//        // 将访问所需资源或用户拥有资源进行比对
//        for (ConfigAttribute configAttribute : configAttributes) {
//            String needAuthority = configAttribute.getAttribute();
//            if (needAuthority == null) {
//                continue;
//            }
//            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
//                if (needAuthority.trim().equals(grantedAuthority.getAuthority())) {
//                    return;
//                }
//            }
//        }
//        throw new AccessDeniedException("抱歉，您没有访问权限");
//    }

    /**
     * 表达式访问决策管理器角色
     *
     * @param authentication   身份验证
     * @param object           对象
     * @since 2023-07-04 10:00:32
     */
//    public void roleExpressionAccessDecisionManager(
//            Authentication authentication,
//            Object object,
//            Collection<ConfigAttribute> configAttributes) {
//        final ConfigAttribute configAttribute = configAttributes.iterator().next();
//        String attribute = configAttribute.getAttribute();
//        final String originScript = attribute;
//
//        // anon 为不需要登录的权限
//        if (attribute.contains("anon")) {
//            return;
//        }
//        // authc 为只要登录了就有权限
//        if (attribute.contains("authc")) {
//            if (authentication == null
//                    || authentication.getPrincipal() == null
//                    || "anonymousUser".equals(authentication.getPrincipal())) {
//                throw new AccessDeniedException("需要登录");
//            }
//            return;
//        }
//
//        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        for (GrantedAuthority authority : authorities) {
//            final String userHasRole = authority.getAuthority();
//            attribute = attribute.replaceAll(userHasRole, "true");
//        }
//
//        // final List<String> allRoles =
//        // roleService.findRoles().stream().toList();
//        final List<String> allRoles = new ArrayList<>();
//        for (String role : allRoles) {
//            attribute = attribute.replaceAll(role, "false");
//        }
//        try {
//            final Object eval = engine.eval(attribute);
//            if (!(eval instanceof Boolean)) {
//                throw new AccessDeniedException(Objects.toString(eval));
//            }
//            if (!((Boolean) eval)) {
//                throw new AccessDeniedException("权限不足2");
//            }
//        } catch (ScriptException e) {
//            LogUtils.error(
//                    "脚本执行错误:[origin:{}][parse:{}][message:{}]",
//                    originScript,
//                    attribute,
//                    e.getMessage());
//            throw new AccessDeniedException("权限不足");
//        }
//    }

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication,
                                                   RequestAuthorizationContext object) {
        return null;
    }
}
