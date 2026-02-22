/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.collections4.MapUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.access.AccessDeniedException
 *  org.springframework.security.access.ConfigAttribute
 *  org.springframework.security.authentication.AnonymousAuthenticationToken
 *  org.springframework.security.authorization.AuthorizationDecision
 *  org.springframework.security.authorization.AuthorizationManager
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.web.FilterInvocation
 *  org.springframework.security.web.access.expression.WebExpressionAuthorizationManager
 *  org.springframework.security.web.access.intercept.RequestAuthorizationContext
 */
package com.kuma.boot.security.spring.authorization;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public class SecurityAuthorizationManager
implements AuthorizationManager<RequestAuthorizationContext> {
    private static final Logger log = LoggerFactory.getLogger(SecurityAuthorizationManager.class);
    private final SecurityMetadataSourceStorage securityMetadataSourceStorage;
    private final SecurityMatcherConfigurer securityMatcherConfigurer;
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = this.manager.getEngineByName("js");
    public static final Pattern pattern = Pattern.compile("(\\w+)");

    public SecurityAuthorizationManager(SecurityMetadataSourceStorage securityMetadataSourceStorage, SecurityMatcherConfigurer securityMatcherConfigurer) {
        this.securityMetadataSourceStorage = securityMetadataSourceStorage;
        this.securityMatcherConfigurer = securityMatcherConfigurer;
    }

    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext object) {
        HttpServletRequest request = object.getRequest();
        String url = request.getRequestURI();
        String method = request.getMethod();
        if (WebUtils.isStaticResources(url)) {
            log.info("Is static resource : [{}], Passed!", (Object)url);
            return new AuthorizationDecision(true);
        }
        if (WebUtils.isPathMatch(this.securityMatcherConfigurer.getPermitAllList(), url)) {
            log.info("Is white list resource : [{}], Passed!", (Object)url);
            return new AuthorizationDecision(true);
        }
        String feignInnerFlag = request.getHeader("ttc-from-inner");
        if (StringUtils.isNotBlank((CharSequence)feignInnerFlag)) {
            log.info("Is feign inner invoke : [{}], Passed!", (Object)url);
            return new AuthorizationDecision(true);
        }
        if (WebUtils.isPathMatch(this.securityMatcherConfigurer.getHasAuthenticatedList(), url)) {
            log.info("Is has authenticated resource : [{}]", (Object)url);
            return new AuthorizationDecision(authenticationSupplier.get().isAuthenticated());
        }
        List<SecurityConfigAttribute> configAttributes = this.findConfigAttribute(url, method, request);
        if (CollectionUtils.isEmpty(configAttributes)) {
            log.info("NO PRIVILEGES : [{}].", (Object)url);
            if (!this.securityMatcherConfigurer.getAuthorizationProperties().getStrict().booleanValue()) {
                Authentication authentication;
                try {
                    authentication = authenticationSupplier.get();
                }
                catch (Exception e) {
                    return new AuthorizationDecision(true);
                }
                if (authentication instanceof AnonymousAuthenticationToken) {
                    log.info("anonymousAuthenticationToken : {}", (Object)url);
                    return new AuthorizationDecision(false);
                }
                if (authentication.isAuthenticated()) {
                    log.info("Request is authenticated: [{}].", (Object)url);
                    return new AuthorizationDecision(true);
                }
            }
            return new AuthorizationDecision(false);
        }
        for (SecurityConfigAttribute configAttribute : configAttributes) {
            WebExpressionAuthorizationManager webExpressionAuthorizationManager = new WebExpressionAuthorizationManager(configAttribute.getAttribute());
            AuthorizationDecision decision = webExpressionAuthorizationManager.check(authenticationSupplier, object);
            if (!decision.isGranted()) continue;
            log.info("Request [{}] is authorized!", (Object)object.getRequest().getRequestURI());
            return decision;
        }
        return new AuthorizationDecision(false);
    }

    private List<SecurityConfigAttribute> findConfigAttribute(String url, String method, HttpServletRequest request) {
        log.info("Current Request is : [{}] - [{}]", (Object)url, (Object)method);
        List<SecurityConfigAttribute> configAttributes = this.securityMetadataSourceStorage.getConfigAttribute(url, method);
        if (CollectionUtils.isNotEmpty(configAttributes)) {
            log.info("Get configAttributes from local storage for : [{}] - [{}]", (Object)url, (Object)method);
            return configAttributes;
        }
        LinkedHashMap<SecurityRequest, List<SecurityConfigAttribute>> compatible = this.securityMetadataSourceStorage.getCompatible();
        if (MapUtils.isNotEmpty(compatible)) {
            for (Map.Entry<SecurityRequest, List<SecurityConfigAttribute>> entry : compatible.entrySet()) {
                SecurityRequestMatcher requestMatcher = new SecurityRequestMatcher(entry.getKey());
                if (!requestMatcher.matches(request)) continue;
                log.info("Request match the wildcard [{}] - [{}]", (Object)entry.getKey(), entry.getValue());
                return entry.getValue();
            }
        }
        return null;
    }

    public AuthorizationDecision accessDecisionVoter(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return new AuthorizationDecision(false);
        }
        Collection authorities = authentication.getAuthorities();
        for (ConfigAttribute attribute : attributes) {
            if (attribute.getAttribute() == null) continue;
            for (GrantedAuthority authority : authorities) {
                if (!attribute.getAttribute().equals(authority.getAuthority())) continue;
                return new AuthorizationDecision(true);
            }
        }
        FilterInvocation fi = (FilterInvocation)object;
        String url = fi.getRequestUrl();
        String matchRoles = "";
        if (StringUtils.isBlank((CharSequence)matchRoles)) {
            return new AuthorizationDecision(false);
        }
        if (matchRoles.contains("anon")) {
            return new AuthorizationDecision(true);
        }
        if (matchRoles.contains("authc") && "anonymousUser".equals(authentication.getPrincipal())) {
            return new AuthorizationDecision(false);
        }
        String originScript = matchRoles;
        for (Object authority : authorities) {
            String userHasRole = authority.getAuthority();
            matchRoles = matchRoles.replaceAll(userHasRole, "true");
        }
        ArrayList allRoles = new ArrayList();
        for (String role : allRoles) {
            matchRoles = matchRoles.replaceAll(role, "false");
        }
        try {
            Matcher matcher = pattern.matcher(originScript);
            while (matcher.find()) {
                for (int i = 0; i < matcher.groupCount(); ++i) {
                    matchRoles = matchRoles.replaceAll(matcher.group(i), "undefined");
                }
            }
        }
        catch (Exception e) {
            LogUtils.warn((String)"\u6b63\u5219\u5339\u914d\u9519\u8bef,\u53ef\u80fd\u811a\u672c\u5b58\u5728\u95ee\u9898:{}", (Object[])new Object[]{e.getMessage()});
        }
        try {
            Object eval = this.engine.eval(matchRoles);
            if (!(eval instanceof Boolean)) {
                LogUtils.error((String)"\u811a\u672c\u6267\u884c\u5931\u8d25,\u8fd4\u56de\u503c\u975e Bool \u7c7b\u578b,\u9ed8\u8ba4\u662f\u7981\u6b62\u8bbf\u95ee:{},{}", (Object[])new Object[]{originScript, matchRoles});
                return new AuthorizationDecision(false);
            }
            if (((Boolean)eval).booleanValue()) {
                // empty if block
            }
            return (Boolean)eval != false ? new AuthorizationDecision(true) : new AuthorizationDecision(false);
        }
        catch (ScriptException e) {
            LogUtils.error((String)"\u811a\u672c\u6267\u884c\u9519\u8bef:[origin:{}][parse:{}][message:{}]", (Object[])new Object[]{originScript, matchRoles, e.getMessage()});
            return new AuthorizationDecision(false);
        }
    }

    public void accessDecisionManager(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        for (ConfigAttribute configAttribute : configAttributes) {
            String needAuthority = configAttribute.getAttribute();
            if (needAuthority == null) continue;
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                if (!needAuthority.trim().equals(grantedAuthority.getAuthority())) continue;
                return;
            }
        }
        throw new AccessDeniedException("\u62b1\u6b49\uff0c\u60a8\u6ca1\u6709\u8bbf\u95ee\u6743\u9650");
    }

    public void roleExpressionAccessDecisionManager(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        String attribute;
        ConfigAttribute configAttribute = configAttributes.iterator().next();
        String originScript = attribute = configAttribute.getAttribute();
        if (attribute.contains("anon")) {
            return;
        }
        if (attribute.contains("authc")) {
            if (authentication == null || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
                throw new AccessDeniedException("\u9700\u8981\u767b\u5f55");
            }
            return;
        }
        Collection authorities = authentication.getAuthorities();
        for (Object authority : authorities) {
            String userHasRole = authority.getAuthority();
            attribute = attribute.replaceAll(userHasRole, "true");
        }
        ArrayList allRoles = new ArrayList();
        for (String role : allRoles) {
            attribute = attribute.replaceAll(role, "false");
        }
        try {
            Object eval = this.engine.eval(attribute);
            if (!(eval instanceof Boolean)) {
                throw new AccessDeniedException(Objects.toString(eval));
            }
            if (!((Boolean)eval).booleanValue()) {
                throw new AccessDeniedException("\u6743\u9650\u4e0d\u8db32");
            }
        }
        catch (ScriptException e) {
            LogUtils.error((String)"\u811a\u672c\u6267\u884c\u9519\u8bef:[origin:{}][parse:{}][message:{}]", (Object[])new Object[]{originScript, attribute, e.getMessage()});
            throw new AccessDeniedException("\u6743\u9650\u4e0d\u8db3");
        }
    }
}

