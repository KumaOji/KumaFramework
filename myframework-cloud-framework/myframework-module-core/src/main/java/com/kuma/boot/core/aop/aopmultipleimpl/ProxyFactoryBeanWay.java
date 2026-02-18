package com.kuma.boot.core.aop.aopmultipleimpl;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ProxyFactoryBeanWay
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class ProxyFactoryBeanWay {


    public static interface CommonDAO {

    }

    public static class PersonService {

        public void save() {
            System.out.println("save method invoke...");
        }
    }

    @Configuration
    public class AppConfig {

        @Bean
        public MethodInterceptor logInterceptor() {
            return new MethodInterceptor() {
                @Override
                public Object invoke( MethodInvocation invocation ) throws Throwable {
                    System.out.println("日志记录...");
                    return invocation.proceed();
                }
            };
        }

        @Bean
        public ProxyFactoryBean personService() throws Exception {
            ProxyFactoryBean proxy = new ProxyFactoryBean();
            proxy.setProxyTargetClass(true);
            proxy.setTargetSource(new SingletonTargetSource(new PersonService()));
            proxy.setProxyInterfaces(new Class<?>[]{CommonDAO.class});
            proxy.setInterceptorNames("logInterceptor");

            //	ProxyFactoryBean proxy = new ProxyFactoryBean() ;
            //// 如果要代理的是目标类，而不是目标类的接口，则为True。如果该属性值设置为true，则创建CGLIB代理
            //	proxy.setProxyTargetClass(false) ;
            //// 控制是否对通过CGLIB创建的代理应用积极优化。除非您完全理解相关AOP代理如何处理优化，否则不应该轻松地使用此设置。目前仅用于CGLIB代理。它对JDK动态代理没有影响。
            //	proxy.setOptimize(false) ;
            //// 如果代理配置被冻结，则不再允许更改配置。无论是作为轻微的优化，还是当您不希望调用者在创建代理后能够操作代理(通过建议的接口)时，这都是有用的。此属性的默认值为false，因此允许更改(例如添加额外的通知)。
            //	proxy.setFrozen(false) ;
            //// 确定是否应该在ThreadLocal中暴露当前代理，以便目标可以访问它。如果目标需要获取代理，而exposeProxy属性被设置为true，那么可以使用AopContext.currentProxy()方法。
            //	proxy.setExposeProxy(false) ;
            //// 接口名称的字符串数组。如果没有提供，则使用目标类的CGLIB代理
            //	proxy.setProxyInterfaces(new Class<?>[] {}) ;
            //// 要应用的Advisor、拦截器或其他Advice名称的字符串数组。点菜很重要，先到先得。也就是说，列表中的第一个拦截器是第一个能够拦截调用的。
            //	proxy.setInterceptorNames("interceptor01") ;
            //// 不管getObject()方法被调用的频率如何，工厂是否应该返回一个对象。有几个FactoryBean实现提供了这样的方法。默认值为true
            //	proxy.setSingleton(true) ;
            return proxy;
        }
    }
}
