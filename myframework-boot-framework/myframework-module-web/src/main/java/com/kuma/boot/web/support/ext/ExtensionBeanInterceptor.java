/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.aopalliance.intercept.Interceptor
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 *  org.springframework.util.Assert
 */
package com.kuma.boot.web.support.ext;

import java.lang.reflect.Field;
import java.util.List;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope(value="prototype")
public class ExtensionBeanInterceptor
implements ApplicationContextAware,
BeanClassLoaderAware,
MethodInterceptor,
FactoryBean<Object> {
    private static final Logger log = LoggerFactory.getLogger(ExtensionBeanInterceptor.class);
    private ApplicationContext applicationContext;
    private ClassLoader classLoader;
    private Object serviceProxy;
    private Class<?> serviceInterface;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String identityStr = this.getIdentity(methodInvocation);
        for (String string : identityStr.split(",")) {
        }
        return null;
    }

    private String getIdentity(MethodInvocation methodInvocation) {
        try {
            Object[] objects = methodInvocation.getArguments();
            if (objects != null && objects.length > 0) {
                Field fields = objects[0].getClass().getDeclaredField("targetSystems");
                fields.setAccessible(true);
                List identityStr = (List)fields.get(objects[0]);
                log.info("getIdentity.identityStr.{}", (Object)identityStr);
                return String.join((CharSequence)",", identityStr);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Object getObject() throws Exception {
        if (this.serviceProxy == null) {
            Class<?> ifc = this.getServiceInterface();
            Assert.notNull(ifc, (String)"Property 'serviceInterface' is required");
            this.serviceProxy = new ProxyFactory(ifc, (Interceptor)this).getProxy(this.classLoader);
        }
        return this.serviceProxy;
    }

    public Class<?> getObjectType() {
        return this.getServiceInterface();
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, (String)"'serviceInterface' must not be null");
        Assert.isTrue((boolean)serviceInterface.isInterface(), (String)"'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceProxy() {
        return this.serviceProxy;
    }

    public void setServiceProxy(Object serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }
}

