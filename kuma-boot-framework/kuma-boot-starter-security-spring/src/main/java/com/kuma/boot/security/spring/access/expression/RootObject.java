package com.kuma.boot.security.spring.access.expression;

import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class RootObject extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;
    private Object target;
    private MethodInvocation methodInvocation;

    @SuppressWarnings("deprecation")
    public RootObject(Authentication authentication) {
        super(authentication);
    }

    @SuppressWarnings("deprecation")
    public RootObject(Supplier<Authentication> authentication) {
        super(authentication);
    }

    public void setThis(Object target) {
        this.target = target;
    }

    public Object getThis() {
        return this.target;
    }

    public Object getFilterObject() {
        return this.filterObject;
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getReturnObject() {
        return this.returnObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public MethodInvocation getMethodInvocation() {
        return this.methodInvocation;
    }

    public void setMethodInvocation(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }
}
