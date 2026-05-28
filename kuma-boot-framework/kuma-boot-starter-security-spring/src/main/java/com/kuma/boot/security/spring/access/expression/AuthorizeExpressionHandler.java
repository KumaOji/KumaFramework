package com.kuma.boot.security.spring.access.expression;

import java.util.Objects;
import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class AuthorizeExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    public EvaluationContext createEvaluationContext(Supplier<? extends @Nullable Authentication> authentication, MethodInvocation mi) {
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(this.createSecurityExpressionRoot((Authentication)authentication.get(), mi), AopUtils.getMostSpecificMethod(mi.getMethod(), AopProxyUtils.ultimateTargetClass(Objects.requireNonNull(mi.getThis()))), mi.getArguments(), this.getParameterNameDiscoverer());
        context.setBeanResolver(this.getBeanResolver());
        return context;
    }

    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        return this.createSecurityExpressionRoot((Supplier<Authentication>) (() -> authentication), invocation);
    }

    @SuppressWarnings("deprecation")
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
