package com.kuma.cloud.uaa.annotation;

import com.kuma.boot.core.utils.context.EnableContextUtils;
import com.kuma.boot.security.spring.annotation.EnableSecurityConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UAA 专用启动注解。
 *
 * <p>与 {@link com.kuma.boot.web.annotation.KumaBootApplication} 的区别：
 * 不引入 {@code Oauth2ResourceAutoConfiguration} 资源服务器 FilterChain，
 * 避免与 Spring Authorization Server 的协议端点链冲突；同时保留框架的方法级
 * {@link com.kuma.boot.security.spring.access.expression.Authorize}、JWT 解码与属性绑定能力。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableContextUtils
@ServletComponentScan(basePackages = {"com.kuma.boot.web.servlet"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableSecurityConfiguration
@SpringBootApplication
public @interface EnableUaaBootApplication {}
