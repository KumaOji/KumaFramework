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

package com.kuma.boot.web.support.ext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 完成代理工厂
 * step1:获取上下文中router
 * step2:获取spring 上下文中动态代理对象
 * step3:实现动态代理 invoke
 * @author baixiu
 * @since 创建时间 2023/12/7 4:47 PM
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ExtensionBeanInterceptor
        implements ApplicationContextAware,
        BeanClassLoaderAware,
        MethodInterceptor,
        FactoryBean<Object> {

    private static final Logger log = LoggerFactory.getLogger(ExtensionBeanInterceptor.class);

    private ApplicationContext applicationContext;

    /**
     * 类加载器
     */
    private ClassLoader classLoader;

    /**
     * 策略路由
     */
    //    private SPIRouter spiRouter;

    /**
     * 代理bean
     */
    private Object serviceProxy;

    /**
     * 扩展服务接口
     */
    private Class<?> serviceInterface;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // step1:get invocation identity
        String identityStr = getIdentity(methodInvocation);
        // step2:push into thread local
        // ThreadLocalSPIRouter.pushIdentity(identityStr);
        // todo step3:通过identity 获取router.可以通过接口实现自定义router形式得到处理。这里出现问题了。先暂时搁置下，排查了两天没看到问题出在哪里。
        // List<String> routerNames=this.spiRouter.route(methodInvocation);
        for (String routerItem : identityStr.split(",")) {
            // step4:通过SPIExtensionBeanContexts 获取 bean
            //            Object
            // routerObject=SPIExtensionBeanContexts.BEAN_EXTENDS_MAP.get(routerItem);
            //            if(Objects.nonNull(routerObject)){
            //                //通过声明class强转object
            //                Object
            // realBean=methodInvocation.getMethod().getDeclaringClass().cast(routerObject);
            //                Method
            // method=realBean.getClass().getMethod(methodInvocation.getMethod().getName(),methodInvocation.getMethod()
            //                        .getParameterTypes());
            //                try {
            //                    //step5:动态代理反射调用。由于是多个调用，不能return
            //                    method.invoke(realBean,methodInvocation.getArguments());
            //                } catch (Exception e) {
            //                    throw new RuntimeException (e);
            //                } finally {
            //                    //ThreadLocalSPIRouter.popIdentity();
            //                }
            //            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getIdentity(MethodInvocation methodInvocation) {
        try {
            Object[] objects = methodInvocation.getArguments();
            if (objects != null && objects.length > 0) {
                Field fields = objects[0].getClass().getDeclaredField("targetSystems");
                fields.setAccessible(true);
                List<String> identityStr = (List<String>) fields.get(objects[0]);
                log.info("getIdentity.identityStr.{}", identityStr);
                return String.join(",", identityStr);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Object getObject() throws Exception {
        if (serviceProxy == null) {
            Class<?> ifc = getServiceInterface();
            Assert.notNull(ifc, "Property 'serviceInterface' is required");
            //            Assert.notNull(getSpiRouter(), "Property 'spiRouter' is required");
            serviceProxy = new ProxyFactory(ifc, this).getProxy(classLoader);
        }
        return serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    //    public SPIRouter getSpiRouter() {
    //        return spiRouter;
    //    }
    //
    //    public void setSpiRouter(SPIRouter spiRouter) {
    //        this.spiRouter = spiRouter;
    //    }

    /**
     * 获取服务接口
     * @param serviceInterface serviceInterface
     */
    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, "'serviceInterface' must not be null");
        Assert.isTrue(serviceInterface.isInterface(), "'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceProxy() {
        return serviceProxy;
    }

    public void setServiceProxy(Object serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }
}
