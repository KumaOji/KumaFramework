/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.AopProxyUtils
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.context.expression.MethodBasedEvaluationContext
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
 *  org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
 *  org.springframework.security.core.Authentication
 */
package com.kuma.boot.security.spring.access.expression;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class AuthorizeExpressionHandler
extends DefaultMethodSecurityExpressionHandler {
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext((Object)this.createSecurityExpressionRoot(authentication, mi), AopUtils.getMostSpecificMethod((Method)mi.getMethod(), (Class)AopProxyUtils.ultimateTargetClass((Object)Objects.requireNonNull(mi.getThis()))), mi.getArguments(), this.getParameterNameDiscoverer());
        context.setBeanResolver(this.getBeanResolver());
        return context;
    }

    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        return this.createSecurityExpressionRoot(() -> authentication, invocation);
    }

    private MethodSecurityExpressionOperations createSecurityExpressionRoot(Supplier<Authentication> authentication, MethodInvocation invocation) {
        RootObject root = new RootObject(authentication);
        root.setMethodInvocation(invocation);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(this.getPermissionEvaluator());
        root.setTrustResolver(this.getTrustResolver());
        root.setRoleHierarchy(this.getRoleHierarchy());
        root.setDefaultRolePrefix(this.getDefaultRolePrefix());
        return root;
    }
}

